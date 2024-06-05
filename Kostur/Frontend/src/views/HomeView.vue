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
      </ul>
    </nav>
    <div class="container">
      <h2>Welcome to our chocolate factory!</h2>
      <p>If you're already a member, please log in:</p>
      <button @click="login">Login</button>
    </div>
    <div class="item" v-for="f in factories" :key="f.id" @click.prevent="goToFactoryInfo(f)">
      <img :src="getFactoryImagePath(f.logo)" :alt="f.alt">
      <div class="details">
        <div class="name">{{ f.name }}</div>
        <div class="location">{{ f.location.address }}</div>
      </div>
      <div class="score">{{ f.rating }}</div>
      <button @click="showFactory(index)">Show</button>
    </div>
  </div>
</template>

<script setup>
  import axios from 'axios';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';


const router = useRouter();

const factories = ref([]);

onMounted(() => {
  fetchFactories();
});

function fetchFactories() {
  axios.get('http://localhost:8080/WebShopAppREST/rest/factories/')
    .then(response => {
      factories.value = response.data;
    })
    .catch(error => {
      console.error('Error loading factories:', error);
      alert('Došlo je do greške prilikom učitavanja fabrika.');
    });
}

function goToFactoryInfo(factory){
        this.error = factory.id;
       router.push({path: '/factoryInfo', query: {id:factory.id}});
   }

   const getFactoryImagePath = (imageName) => `/assets/FactoryLogos/${imageName}`;

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
