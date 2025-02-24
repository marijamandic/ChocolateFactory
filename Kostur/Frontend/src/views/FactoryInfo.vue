<template>
  <div>

    <div class="container" v-if="factory">
      <!-- Prikaz informacija o fabrici -->
      <h2>{{ factory.name }}</h2>
      <img :src="getFactoryImagePath(factory.logo)" :alt="factory.alt">

      <table class="factory-info">
        <tr>
          <td class="label">Visiting Time:</td>
          <td>{{ formatTime(factory.openTime) }} - {{ formatTime(factory.closeTime) }} 
            <span class="opened" v-if="isFactoryOpen(factory.openTime, factory.closeTime)">OPEN</span>
            <span class="closed" v-else>CLOSED</span>
          </td>
        </tr>
        <tr>
          <td class="label">Location:</td>
          <td>{{ factory.location.address }}</td>
        </tr>
        <tr>
          <td class="label">Rating:</td>
          <td>{{ factory.rating }}</td>
        </tr>
      </table>

      <button @click="startShopping">Start shopping!</button>

      <div class="chocolates">
        <h3>Chocolates:</h3>
        <table v-if="chocolates.length > 0" class="table-container">
          <table class="chocolates-table">
            <tr>
              <td>Name</td>
              <td>Description</td>
              <td>Kind</td>
              <td>Price</td>
              <td>Quantity</td>
              <td>Type</td>
            </tr>
            <tr v-for="(chocolate, index) in chocolates" :key="chocolate.uuid" 
                :class="{'light-row': index % 2 === 0, 'dark-row': index % 2 !== 0}">
              <td>{{ chocolate.name }}</td>
              <td>{{ chocolate.description }}</td>
              <td>{{ getChocolateKind(chocolate) }}</td>
              <td>{{ chocolate.price }}</td>
              <td>{{ chocolate.quantity }}</td>
              <td>{{ getChocolateType(chocolate) }}</td>
              <td><button @click="addToCart(chocolate.id)">Add to cart</button></td>
            </tr>
          </table>
        </table>
        <p v-else>No chocolates available.</p>
      </div>

      <div class="comments">
        <h3>Comments:</h3>
        
        <div v-if="comments.length > 0">
          <div v-for="(comment, index) in comments" :key="comment.id" class="comment">
            <p><strong>{{ comment.customerId }}:</strong> {{ comment.text }}
              <span class="stars">
                <span v-for="i in 5" :key="i" 
                      :class="{'star': true, 'filled': i <= comment.rating}">
                  ★
                </span>
              </span>
            </p>
          </div>
        </div>
        <p v-else>No comments yet</p>
      </div>
      
      <!-- Ostatak informacija o fabrici -->
      <!-- Dugme za dodavanje čokolade -->
      <!-- <button @click="goToAddChocolate">Add Chocolate</button> -->
    </div>
    <div v-else>
      <p>Loading...</p>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios';
import { onMounted, ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();
const factory = ref(null);
const chocolates = ref([]);
const comments = ref([]);
const isAdminLoggedIn = ref(false); 
const isManagerLoggedIn = ref(false);
const isCustomerLoggedIn = ref(false);
const shoppingCart = ref(null);

onMounted(() => {
  fetchFactory();
  fetchComments();
  wichRoleIsLoggedIn();
});

const getFactoryImagePath = (imageName) => `/assets/FactoryLogos/${imageName}`;

function fetchFactory() {
  const factoryId = route.params.id;
  axios.get(`http://localhost:8080/WebShopAppREST/rest/factories/${factoryId}`)
    .then(response => {
      factory.value = response.data;
      chocolates.value = factory.value.chocolates.map(chocolate => {
        return { ...chocolate, uuid: generateUUID() };
      });
    })
    .catch(error => {
      console.error('Error loading factory:', error);
      alert('Došlo je do greške prilikom učitavanja fabrike.');
    });
}

function fetchComments() {
  const factoryId = route.params.id;
  axios.get(`http://localhost:8080/WebShopAppREST/rest/comments/${factoryId}`)
    .then(response => {
      comments.value = response.data;
    })
    .catch(error => {
      console.error('Error loading comments:', error);
      alert('Došlo je do greške prilikom učitavanja komentara.');
    });
}

// Funkcija za generisanje jedinstvenog identifikatora
function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    var r = Math.random() * 16 | 0,
      v = c == 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

function getChocolateType(chocolate) {
  let message;
  switch (chocolate.type) {
    case "ORDINARY":
      message = "Ordinary";
      break;
    case "FOR_COOKING":
      message = "For cooking";
      break;
    case "FOR_DRINKING":
      message = "For drinking";
      break;
    case "GIFT":
      message = "Gift";
      break;
    default:
      message = "Unknown type";
  }
  return message;
}

function getChocolateKind(chocolate) {
  let message;
  switch (chocolate.kind) {
    case "DARK":
      message = "Dark";
      break;
    case "MILK":
      message = "Milk";
      break;
    case "WHITE":
      message = "White";
      break;
    case "FLAVORED":
      message = "Flavored";
      break;
    default:
      message = "Unknown kind";
  }
  return message;
}

function goToAddChocolate() {
  router.push({ path: '/add-chocolate' });
}

function isFactoryOpen(openTime, closeTime) {
  const currentTime = new Date();
  const [openHour, openMinute] = openTime.split(':').map(Number);
  const [closeHour, closeMinute] = closeTime.split(':').map(Number);

  const openDate = new Date(currentTime);
  openDate.setHours(openHour, openMinute);

  const closeDate = new Date(currentTime);
  closeDate.setHours(closeHour, closeMinute);

  return currentTime >= openDate && currentTime <= closeDate;
}

function formatTime(time) {
  const [hour, minute] = time.split(':');
  return `${hour}:${minute}`;
}

async function wichRoleIsLoggedIn() {
    const isLoggedIn = localStorage.getItem("jwtToken");

    if (isLoggedIn) {
      const loggedInUser = JSON.parse(localStorage.getItem("loggedInUser"));
      const username = loggedInUser.username; 

      try {
        const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/users/getByUsername/${username}`);
        const userRole = response.data.role;

        if (userRole === 'ADMIN') {
          console.log("User is an admin!");
          isAdminLoggedIn.value = true;
        } else if(userRole === 'MANAGER') {
          console.log("User is a manager!");
          isManagerLoggedIn.value = true; 
        } else if(userRole === 'CUSTOMER'){
          console.log("User is a customer!");
          isCustomerLoggedIn.value = true;
        }
      } catch (error) {
        console.error("Error fetching user data:", error);
        return false; 
      }
    }

    return false; 
  }

  async function startShopping(){
    const isLoggedIn = localStorage.getItem("jwtToken");

    if (isLoggedIn) {
      const loggedInUser = JSON.parse(localStorage.getItem("loggedInUser"));
      const username = loggedInUser.username; 

      try {
        const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/users/getByUsername/${username}`);
        const user = response.data;

        const requestBody = {
          chocolates: [],
          user: {
            id: user.id
          }
        };

        console.log("request body: ", requestBody);
      const cartResponse = await axios.post("http://localhost:8080/WebShopAppREST/rest/shoppingCarts/create", requestBody);
      
      console.log("Shopping cart created successfully:", cartResponse.data);
      shoppingCart.value = cartResponse.data;
      localStorage.setItem("shoppingCartId", shoppingCart.value.id);
      console.log("local storage: ", localStorage.getItem("shoppingCartId"));
      return cartResponse.data;

      } catch (error) {
        console.error("Error creating shopping cart:", error);
        return false; 
      }
    }
}

async function addToCart(chocolateId){
  try {
    const response = await axios.put(`http://localhost:8080/WebShopAppREST/rest/shoppingCarts/add/${shoppingCart.value.id}/${chocolateId}`);
    shoppingCart.value = response.data;
    console.log("updated cart: ", shoppingCart.value);

  } catch (error) {
    console.error("Error adding to cart:", error);
    return false; 
  }
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

.container {
  margin: 20px;
  text-align: center;
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

h2 {
  font-size: 60px;
  font-family: 'Times New Roman', Times, serif;
  text-align: center;
  color: rgb(210, 160, 120);
  width: 100%;
}

h3{
  font-size: 30px;
  font-family: 'Times New Roman', Times, serif;
  text-align: center;
  color: rgb(210, 160, 120);
  width: 100%;
  margin-bottom: 10px;
  margin-top: 40px
}

.closed{
  font-size:20px;
  font-weight: bold;
  color:white;
  background-color: red;
  border-radius: 5px;
  margin-left: 20px;
  padding:5px;
}

.opened{
  font-size:20px;
  font-weight: bold;
  color:white;
  background-color: green;
  border-radius: 5px;
  margin-left: 20px;
  padding: 5px;
}

.factory-info {
  width: auto;
  margin: auto;
  border-collapse: collapse;
}

.factory-info tr:nth-child {
  background-color: white;
}

.factory-info td {
  padding: 10px;
  font-size: 20px;
}

.label {
  font-weight: bold;
  text-align: left;
  width: 40%;
}

.table-container {
  display: flex;
  justify-content: center;
  font-family: 'Open Sans', sans-serif;
}

.chocolates-table {
  width: 80%;
  border-collapse: separate;
  border-spacing: 0;
  border-radius: 15px;
  overflow: hidden;
  justify-content: center;
}
.chocolates-table th, .chocolates-table td {
  padding: 8px;
  text-align: left;
}
.chocolates-table th {
  background-color: rgb(230, 200, 180);
}

.light-row {
  background-color: rgba(210, 161, 120, 0.119);
}

.dark-row {
  background-color: rgb(210, 160, 120);
}

.star {
  color: #ccc;
  font-size: 1.5em;
  margin-right: 5px;
}

.star.filled {
  color: #FFD700;
}

.comments{
  font-size: 15px;
  font-family: 'Open Sans', sans-serif;
}
</style>

