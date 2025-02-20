<template>
    <div class="login-form">
      <h2>Login</h2>
      <form @submit.prevent="login">
        <div>
          <label for="username">Username</label>
          <input type="text" id="username" v-model="user.username" required />
        </div>
        <div>
          <label for="password">Password</label>
          <input type="password" id="password" v-model="user.password" required />
        </div>
        <button type="submit">Login</button>
      </form>
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  
  export default {
    setup() {
      const user = ref({
        username: '',
        password: '',
      });
      const errorMessage = ref('');
      const router = useRouter();
  
      async function login() {
        try {
          const requestData = {
            username: user.value.username, 
            password: user.value.password
          };
  
          console.log("Sending request:", requestData);  // Debugging log
  
          const response = await axios.post(
            "http://localhost:8080/WebShopAppREST/rest/users/login", 
            requestData,
            {
              headers: {
                "Content-Type": "application/json"
              }
            }
          );
  
          console.log("Response:", response.data);
  
          if (response.data.token) {
            localStorage.setItem("jwtToken", response.data.token);
            localStorage.setItem("loggedInUser", JSON.stringify({
              username: user.value.username
            }));
            
            router.push("/");
          }
          else {
            errorMessage.value = "Login failed. No token received.";
          }
        } catch (error) {
          console.error("Login error:", error);
          errorMessage.value = "Invalid credentials, please try again.";
        }
      }
  
      return { user, errorMessage, login };
    }
  };
  </script>
  
  <style scoped>
  .login-form {
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
  
  .login-form h2 {
    text-align: center;
    display: block;
    margin-bottom: 15px;
    color: white;
    font-weight: bold;
    font-size: 25px;
  }
  
  .login-form div {
    margin-bottom: 10px;
  }
  
  .login-form label {
    display: block;
    margin-bottom: 5px;
    color: white;
    font-size: 15px;
    font-weight: bold;
  }
  
  .login-form input,
  .login-form select {
    width: 300px;
    padding: 8px;
    box-sizing: border-box;
    border-radius: 15px;
  }
  
  .login-form button {
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
  
  .login-form button:disabled {
    background-color: #ccc;
  }
  
  .error-message {
    color: red;
  }
  </style>
  