<template>
  <!-- <nav>
    <router-link to="/">Home</router-link> |
    <router-link to="/about">About</router-link>
  </nav> -->
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
        <li><router-link to="/registration">Sign in</router-link></li>
        <button v-if="isLoggedIn" @click="logout()">Logout</button>
        <li v-if="isAdminLoggedIn"><router-link to="/registration">Add manager</router-link></li>
        <li v-if="isManagerLoggedIn"><router-link to="/registration">Add worker</router-link></li>
      </ul>
    </nav>

    <div v-if="!isLoggedIn" class="welcome-container">
      <h2>Welcome to our chocolate factory!</h2><br>
      <p>If you're already a member, please log in:</p><br>
      <button @click="goToLogin()">Log in</button>
    </div>

    <router-view/>
  </div>
</template>

<script setup>
  import axios from 'axios';
  import { onMounted, ref, computed } from 'vue';
  import { useRouter } from 'vue-router';

  const router = useRouter();
  const isLoggedIn = computed(() => !!localStorage.getItem("jwtToken"));
  const isAdminLoggedIn = ref(false); 
  const isManagerLoggedIn = ref(false);


  onMounted(async () => {
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
   // window.location.reload();
  }

</script>

<style scoped>
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

</style>