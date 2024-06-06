<template>
  <div>
    <header>
      <h1>Chocolate Factory</h1>
    </header>
    <nav>
      <ul>
        <li><router-link to="/">Home</router-link></li>
        <li><router-link to="/add-chocolate">Ne klikci ovde</router-link></li>
        <li><router-link to="/about">About Us</router-link></li>
        <li><router-link to="/contact">Contact</router-link></li>
      </ul>
    </nav>
    <div class="container" v-if="factory">
      <!-- Prikaz informacija o fabrici -->
      <img :src="getFactoryImagePath(factory.logo)" :alt="factory.alt">
      <h2>{{ factory.name }}</h2>
      <p>Visiting Time: {{ formatTime(factory.openTime) }} - {{ formatTime(factory.closeTime) }} 
        <span v-if="isFactoryOpen(factory.openTime, factory.closeTime)"> (Open Now)</span>
        <span v-else> (Closed Now)</span>
      </p>
      <p>Location: {{ factory.location.address }}</p>
      <p>Rating: {{ factory.rating }}</p>

      <h3>Chocolates:</h3>
      <ul v-if="chocolates.length > 0" class="chocolates-list">
        <li v-for="chocolate in chocolates" :key="chocolate.uuid" class="chocolate-item">
          <!-- Prikaz čokolade -->
          <div>{{ chocolate.name }}</div>
          <button @click="deleteChocolate(chocolate.uuid)">Delete</button>
        </li>
      </ul>
      <p v-else>No chocolates available.</p>

      <h3>Comments:</h3>
      <p>There is no comments yet</p>

      
      <!-- Ostatak informacija o fabrici -->
      <!-- Dugme za dodavanje čokolade -->
      <button @click="goToAddChocolate">Add Chocolate</button>
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
const isDeleted = ref(false);

onMounted(() => {
  // Učitavanje informacija o fabrici
  fetchFactory();
});

const getFactoryImagePath = (imageName) => `/assets/FactoryLogos/${imageName}`;

function fetchFactory() {
  const factoryId = route.params.id;
  axios.get(`http://localhost:8080/WebShopAppREST/rest/factories/${factoryId}`)
    .then(response => {
      factory.value = response.data;
      chocolates.value = factory.value.chocolates.map(chocolate => {
        return { ...chocolate, uuid: generateUUID() }; // Dodavanje uuid-a za svaku čokoladu
      });
    })
    .catch(error => {
      console.error('Error loading factory:', error);
      alert('Došlo je do greške prilikom učitavanja fabrike.');
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

// Funkcija za brisanje čokolade sa odabranim uuid-om
function deleteChocolate(chocolateId) {
  // Pronalaženje indeksa čokolade sa datim uuid-om
  const index = chocolates.value.findIndex(chocolate => chocolate.uuid === chocolateId);
  if (index !== -1) {
    chocolates.value.splice(index, 1); // Uklanjanje čokolade iz liste
  }
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
</script>


<style scoped>
body {
  font-family: Arial, sans-serif;
  margin: 0;
  padding: 0;
}

header {
  background-color: #333;
  color: white;
  padding: 10px;
  text-align: center;
}

nav {
  background-color: #666;
  padding: 10px;
}

nav ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
  text-align: center;
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
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
}

button:hover {
  background-color: #45a049;
}

.item {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  margin-left: 40px; /* Dodata margina sa leve strane */
  margin-right: 40px; /* Dodata margina sa desne strane */
  padding-left: 20px; /* Dodata unutrašnja margina sa leve strane */
  padding-right: 20px; /* Dodata unutrašnja margina sa desne strane */
}

.item img {
  width: 150px;
  height: 150px; /* Povećana veličina slike */
  margin-right: 20px;
}

.details {
  flex: 1;
}

.name {
  font-weight: bold;
  margin-bottom: 5px;
  margin-top: 5px; /* Dodata razdaljina između naziva i ocene */
}

.location {
  font-style: italic;
  color: #888;
  margin-bottom: 5px;
  margin-top: 5px; /* Dodata razdaljina između naziva i ocene */
}

.score {
  font-weight: bold;
  font-size: 20px;
  margin-right: 10px;
  color: #4CAF50;
  margin-top: 5px; /* Dodata razdaljina između naziva i ocene */
}

.chocolate-item {
  margin-bottom: 20px; /* Dodat razmak od 100 piksela ispod svake stavke čokolade */
}

.chocolate-details {
  margin-right: 100px; /* Dodat razmak od 100 piksela između naziva i dugmeta */
}

.chocolate-details button {
  margin-left: 100px; /* Dodat razmak od 100 piksela s leve strane dugmeta */
}
</style>

