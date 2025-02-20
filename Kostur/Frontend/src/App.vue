<template>
  <div>
    <header>
      <h1>Chocolate Factory</h1>
    </header>

    <nav>
      <ul>
        <li><router-link to="/">Home</router-link></li>
        <li><router-link to="/products">Products</router-link></li>
        <li><router-link to="/about">About Us</router-link></li>
        <li><router-link to="/contact">Contact</router-link></li>
        <li>
          <a href="#" @click.prevent="toggleDropdown">
            <span class="material-icons profile-icon">account_circle</span>
          </a>
          <ul v-if="dropdownVisible" class="dropdown-menu">
            <li v-if="isLoggedIn"><router-link to="/profile">See Profile</router-link></li>
            <li v-if="!isLoggedIn"><router-link to="/login">Login</router-link></li>
            <li v-if="isLoggedIn"><router-link to="#" @click.prevent="logout">Logout</router-link></li>
            <li v-if="!isLoggedIn"><router-link to="/registration">Register</router-link></li>
            <li v-if="isAdminLoggedIn"><router-link to="/registration">Add manager</router-link></li>
            <li v-if="isManagerLoggedIn"><router-link to="/registration">Add worker</router-link></li>
          </ul>
        </li>
      </ul>
    </nav>
    <router-view/>
  </div>
</template>

<script setup>
  import axios from 'axios';
  import { onMounted, ref, computed , watchEffect} from 'vue';
  import { useRouter } from 'vue-router';

  const router = useRouter();
 // const isLoggedIn = computed(() => !!localStorage.getItem("jwtToken"));
 //const isLoggedIn = ref(false); 
  const isLoggedIn = ref(!!localStorage.getItem("jwtToken"));
  const isAdminLoggedIn = ref(false); 
  const isManagerLoggedIn = ref(false);
  const dropdownVisible = ref(false);

  watchEffect(() => {
    isLoggedIn.value = !!localStorage.getItem("jwtToken");
  });

  onMounted(async () => {
    //isLoggedIn.value = !!localStorage.getItem("jwtToken");
    const token = localStorage.getItem("jwtToken");
    const loggedInUser = localStorage.getItem("loggedInUser");

      // Ako postoji token i korisnik u localStorage, postavljamo isLoggedIn na true
    isLoggedIn.value = !!token && !!loggedInUser;
    console.log("is logged in: ", isLoggedIn.value);
    isAdminLoggedIn.value = await isAdmin();
    isManagerLoggedIn.value = await isManager();
  });

  function goToLogin(){
    router.push({ path: `/login` });
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

  function logout() {
    console.log("logged in user: ", localStorage.getItem("loggedInUser"));

    localStorage.removeItem("jwtToken");
    localStorage.removeItem("loggedInUser");
    console.log("logged in user: ", localStorage.getItem("loggedInUser"));
    isLoggedIn.value = false;  // Resetujemo vrednost
    router.push("/");
    //window.location.reload();
  }

  function toggleDropdown(){
    dropdownVisible.value = !dropdownVisible.value;
  }
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Parisienne&display=swap');

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: Arial, sans-serif;
  margin: 0;
  padding: 0;
}

header {
  display: flex;
  justify-content: center; 
  align-items: center;
  height: 200px;
  background-color: rgb(129, 70, 41);
}

h1 {
  font-size: 80px;
  font-family: 'Parisienne', cursive;
  text-align: center;
  color: white;
  width: 100%;
}

nav {
  display: flex;
  background-color: rgb(210, 160, 120);
  height: 40px;
  align-items: center;
  justify-content: center; 
  margin-bottom: 80px;
}

nav ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
  text-align: center;
  font-size: 20px;
}

nav ul li {
  display: inline;
  margin-right: 20px;
  position: relative;
}

nav ul li a {
  text-decoration: none;
  color: white;
}

nav ul li a:hover {
  text-decoration: underline;
}

.welcome-container {
  margin-top: 10px;
  text-align: center;
  font-size: 20px;
  background-color: white;
  color:rgb(129, 70, 41);
  height: 150px;
  margin-bottom: 10px;
}

button {
  padding: 10px 20px;
  background-color: white;
  color: rgb(129, 70, 41);
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  border: 2px solid rgb(129, 70, 41);
}

button:hover {
  background-color: rgb(210, 160, 120);
  color:white;
}

ul.dropdown-menu {
  list-style-type: none;
  padding: 0;
  margin: 0;
  position: absolute; 
  background-color: white;
  box-shadow: 0px 8px 16px rgba(0, 0, 0, 0.2);
  z-index: 1;
  width: 150px; 
  top: 100%; 
  left: 0;
}

ul.dropdown-menu li {
  padding: 5px;
  border-bottom: none;
}

ul.dropdown-menu li:last-child {
  border-bottom: none;
}

ul.dropdown-menu li a {
  color: rgb(210, 160, 120);
  text-decoration: none;
  display: block; 
  padding: 8px;
}

ul.dropdown-menu li a:hover {
  background-color: rgb(210, 160, 120);
  color: white;
}

.profile-icon {
  font-size: 30px;
  margin-left: auto;
  vertical-align: middle;
}
</style>