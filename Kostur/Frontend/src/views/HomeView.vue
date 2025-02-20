<template>
  <div class="item" v-for="f in factories" :key="f.id" @click.prevent="goToFactoryInfo(f)">
    <img :src="getFactoryImagePath(f.logo)" :alt="f.alt">
    <div class="details">
      <div class="name">{{ f.name }}</div>
      <div class="location">{{ f.location.address }}</div>
    </div>
    <div class="badge">★</div>
    <div class="score">{{ f.rating }}</div>
    <button @click="showFactory(index)">Show</button>
  </div>
</template>

<script setup>
import axios from 'axios';
import { onMounted, ref, computed } from 'vue';
import { useRouter } from 'vue-router';


const router = useRouter();
const factories = ref([]);

onMounted(() => {
fetchFactories();
});

function fetchFactories() {
axios.get('http://localhost:8080/WebShopAppREST/rest/factories/')
.then(response => {
    factories.value = response.data.sort((a, b) => {
      return b.open - a.open;
    });
  })
  .catch(error => {
    console.error('Error loading factories:', error);
    alert('Došlo je do greške prilikom učitavanja fabrika.');
  });
}

function goToFactoryInfo(factory){
      this.error = factory.id;
//  router.push({path: '/factoryInfo', query: {id:factory.id}});
    router.push({ path: `/factoryInfo/${factory.id}` });
}
const getFactoryImagePath = (imageName) => `/assets/FactoryLogos/${imageName}`;

function showFactory(index) {

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

.item {
display: flex;
width: 75%;
height:250px;
align-items: center;
justify-content: center;
background-color: rgb(210, 160, 120);
margin: 0 auto;
border-radius: 15px;
margin-bottom: 10px;
padding-left: 30px;
padding-right: 30px;
padding-bottom:7px;
padding-top:7px;
}

.item img {
width: 200px;
height: 200px;
margin-right: 20px;
border-radius: 15px;
}

.details {
flex: 1;
}

.name {
font-weight: bold;
margin-bottom: 5px;
margin-top: 5px; 
color: white;
font-size: 45px;
font-family: 'Times New Roman', Times, serif;
}

.location {
font-style: italic;
color: white;
margin-bottom: 5px;
margin-top: 5px;
font-size: 20px;
}

.score {
font-weight: bold;
font-size: 30px;
color: white;
margin-right: 20px;
margin-top: 5px;
}

.badge {
font-size: 40px;
color: white;
background-color: rgb(210, 160, 120);
display: inline-block;
text-align: center;
width: 60px;
height: 60px;
font-weight: bold;
}

</style>
