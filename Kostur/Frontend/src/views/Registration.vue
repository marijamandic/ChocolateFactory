<template>
    <div class="registration-form">
      <h2>Registracija</h2>
      <form @submit.prevent="registerUser">
        <div>
          <label for="username">Username:</label>
          <input type="text" id="username" v-model="user.username" @input="checkUsernameAvailability" required />
          <div v-if="usernameTaken" class="error-message">Username not available</div>
        </div>
  
        <div>
          <label for="password">Password:</label>
          <input type="password" id="password" v-model="user.password" required />
        </div>
  
        <div>
          <label for="confirmPassword">Confirm password:</label>
          <input type="password" id="confirmPassword" v-model="confirmPassword" required />
          <div v-if="passwordMismatch" class="error-message">Passwords do not match</div>
        </div>
  
        <div>
          <label for="firstName">Name:</label>
          <input type="text" id="firstName" v-model="user.name" required />
        </div>
  
        <div>
          <label for="lastName">Surname:</label>
          <input type="text" id="lastName" v-model="user.surname" required />
        </div>
  
        <div>
          <label for="gender">Pol:</label>
          <select id="gender" v-model="user.gender" required>
            <option value="MALE">Male</option>
            <option value="FEMALE">Female</option>
          </select>
        </div>
  
        <div>
          <label for="dob">Birthday:</label>
          <input type="date" id="dob" v-model="user.birthday" required />
        </div>
  
        <div v-if="isAdminLoggedIn">
          <label for="factory">Factory:</label>
          <select id="factory" v-model="user.factory" required>
            <option v-for="factory in factories" :key="factory.id" :value="factory">{{ factory.name }}</option>
          </select>
        </div>
  
        

        <button type="submit" :disabled="passwordMismatch || usernameTaken">Register</button>
      </form>
    </div>
  </template>
  
  <script setup>
  import axios from 'axios';
  import { ref, watch, onMounted } from 'vue';
  
  const user = ref({
    username: '',
    password: '',
    name: '',
    surname: '',
    gender: 'MALE',
    birthday: '',
    role: 'CUSTOMER',
    score: 0,
    factory: {
      closeTime: '',
      id: '',
      location: {
        address: '',
        id: '',
        latitude: 0.0,
        longitude: 0.0
      },
      logo: '',
      name: '',
      open: false,
      openTime: '',
      rating: 0.0
    },
    customerType: {
      discount: 0.1,
      id: '2',
      score: 100,
      type: 'BRONZE'
    }
  });
  
  const confirmPassword = ref('');
  const passwordMismatch = ref(false);
  const factories = ref([]); 
  const isAdminLoggedIn = ref(false); 
  const usernameTaken = ref(false);
  
  
  watch([user.value.password, confirmPassword], () => {
    passwordMismatch.value = user.value.password !== confirmPassword.value;
  });
  
  onMounted(async () => {
    isAdminLoggedIn.value = await isAdmin(); 
    if (isAdminLoggedIn.value) {
      await fetchFactories();
    }
  });
  
  async function checkUsernameAvailability() {
  if (!user.value.username) {
    usernameTaken.value = false;
    return;
  }

  try {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/users/check-username/${user.value.username}`);
    usernameTaken.value = response.data;
    console.log("usernameTaken: ", usernameTaken.value);
  } catch (error) {
    console.error("Greška prilikom provere username-a:", error);
    usernameTaken.value = false;
  }
}
  
  async function registerUser() {
    if (passwordMismatch.value) {
      alert('Lozinke se ne poklapaju!');
      return;
    }
  
    const userData = {
      username: user.value.username,
      password: user.value.password,
      name: user.value.name,
      surname: user.value.surname,
      gender: user.value.gender,
      birthday: user.value.birthday,
      role: user.value.role,
      score: user.value.score,
    };
  
    if (isAdminLoggedIn.value) {
      userData.role = 'MANAGER';
      userData.factory = user.value.factory; 
    } else if (await isManager()) {
      userData.role = 'WORKER';
    }
    else{
      userData.customerType = user.value.customerType;
    }
  
    try {
      const response = await axios.post('http://localhost:8080/WebShopAppREST/rest/users/add', userData);
      console.log('Uspešno registrovan korisnik:', response.data);
      alert('User successfully registrated');
    } catch (error) {
      console.error('Greška prilikom registracije:', error);
      alert('Error while registering the user');
    }
  }
  
  async function fetchFactories() {
    try {
      const response = await axios.get('http://localhost:8080/WebShopAppREST/rest/factories');
      factories.value = response.data;
    } catch (error) {
      console.error("Greška prilikom učitavanja fabrika:", error);
    }
  }
  
  async function isAdmin() {
    const isLoggedIn = localStorage.getItem("jwtToken");
  
    if (isLoggedIn) {
      const loggedInUser = JSON.parse(localStorage.getItem("loggedInUser"));
      const username = loggedInUser.username;
  
      try {
        const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/users/getByUsername/${username}`);
        const userRole = response.data.role;
  
        if (userRole === 'ADMIN') {
          console.log("User is an admin!");
          return true;
        } else {
          console.log("User is not an admin.");
          return false;
        }
      } catch (error) {
        console.error("Error fetching user data:", error);
        return false;
      }
    }
  
    return false;
  }
  
  async function isManager() {
    const isLoggedIn = localStorage.getItem("jwtToken");
  
    if (isLoggedIn) {
      const loggedInUser = JSON.parse(localStorage.getItem("loggedInUser"));
      const username = loggedInUser.username;
  
      try {
        const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/users/getByUsername/${username}`);
        const userRole = response.data.role;
  
        if (userRole === 'MANAGER') {
          console.log("User is a manager!");
          return true;
        } else {
          console.log("User is not a manager.");
          return false;
        }
      } catch (error) {
        console.error("Error fetching user data:", error);
        return false;
      }
    }
  
    return false;
  }
  </script>
  
  <style scoped>
  .registration-form {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    max-width: 500px;
    margin: 0 auto;
    padding: 10px;
    border: 1px solid rgb(129, 70, 41);
    border-radius: 8px;
    background-color: rgb(210, 160, 120);
  }
  
  .registration-form h2 {
    text-align: center;
    display: block;
    margin-bottom: 15px;
    color: white;
    font-weight: bold;
    font-size: 25px;
  }
  
  .registration-form div {
    margin-bottom: 10px;
  }
  
  .registration-form label {
    display: block;
    margin-bottom: 5px;
    color: white;
    font-size: 15px;
    font-weight: bold;
  }
  
  .registration-form input,
  .registration-form select {
    width: 300px;
    padding: 8px;
    box-sizing: border-box;
    border-radius: 15px;
  }
  
  .registration-form button {
    padding: 10px 20px;
    background-color: rgb(129, 70, 41);
    color: white;
    border-radius: 5px;
    cursor: pointer;
    font-size: 16px;
    width: 300px;
    border-radius: 15px;
    margin-top: 15px;
    border: 2px solid rgb(129, 70, 41);
  }
  
  .registration-form button:disabled {
    background-color: #ccc;
  }
  
  .error-message {
    color: red;
    font-size: 12px;
  }
  </style>
  