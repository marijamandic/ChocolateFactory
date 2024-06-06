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
      <p>Location: {{ factory.location.address }}</p>
      <p>Rating: {{ factory.rating }}</p>
      
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

onMounted(() => {
  // Učitavanje informacija o fabrici
  fetchFactory();
});

const getFactoryImagePath = (imageName) => `/assets/FactoryLogos/${imageName}`;

function fetchFactory() {
  const factoryId = route.params.id; // Čitanje ID fabrike iz dinamičkog segmenta rute
  axios.get(`http://localhost:8080/WebShopAppREST/rest/factories/${factoryId}`)
    .then(response => {
      factory.value = response.data;
    })
    .catch(error => {
      console.error('Error loading factory:', error);
      alert('Došlo je do greške prilikom učitavanja fabrike.');
    });
}

function goToAddChocolate() {
  router.push({ path: '/add-chocolate' });
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
</style>

