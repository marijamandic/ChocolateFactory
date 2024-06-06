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
    <div class="container">
      <h2>Add New Chocolate</h2>
      <form @submit.prevent="addChocolate">
        <div>
          <label for="name">Name:</label>
          <input type="text" v-model="name" required>
        </div>
        <div>
          <label for="price">Price:</label>
          <input type="number" v-model="price" required>
        </div>
        <div>
          <label for="type">Type:</label>
          <select v-model="type" required>
            <option value="ORDINARY">Ordinary</option>
            <option value="FOR_COOKING">For Cooking</option>
            <option value="FOR_DRINKING">For Drinking</option>
            <option value="GIFT">Gift</option>
          </select>
        </div>
        <div>
          <label for="kind">Kind:</label>
          <select v-model="kind" required>
            <option value="DARK">Dark</option>
            <option value="MILK">Milk</option>
            <option value="WHITE">White</option>
            <option value="FLAVORED">Flavored</option>
          </select>
        </div>
        <div>
          <label for="weight">Weight (grams):</label>
          <input type="number" v-model="weight" required>
        </div>
        <div>
          <label for="description">Description:</label>
          <textarea v-model="description" required></textarea>
        </div>
        <div>
          <label for="image">Image:</label>
          <input type="text" v-model="image" required>
        </div>
        <button type="submit">Add Chocolate</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const name = ref('');
const price = ref(0);
const type = ref('ORDINARY');
const kind = ref('DARK');
const weight = ref(0);
const description = ref('');
const image = ref('');

const router = useRouter();
const chocolateList = ref([]);

function addChocolate() {
  const chocolate = {
    name: name.value,
    price: price.value,
    type: type.value,
    kind: kind.value,
    weight: weight.value,
    description: description.value,
    image: image.value,
    inStock: false,
    quantity: 0,
    isDeleted: false
  };

  axios.post('http://localhost:8080/WebShopAppREST/rest/chocolates', chocolate, {
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
      alert('Chocolate added successfully!');
      // Clear the form
      name.value = '';
      price.value = 0;
      type.value = 'ORDINARY';
      kind.value = 'DARK';
      weight.value = 0;
      description.value = '';
      image.value = '';

        chocolateList.value.push(response.data); // Dodavanje novog proizvoda u listu
       router.push({ name: 'about'});
    })
    .catch(error => {
      console.error('Error adding chocolate:', error);
      alert('There was an error adding the chocolate.');
    });
}
</script>

<style scoped>
/* Stilovi za formu */
.container {
  margin: 20px;
}

form div {
  margin-bottom: 10px;
}

label {
  display: block;
  margin-bottom: 5px;
}

input, select, textarea {
  width: 30%;
  padding: 8px;
  box-sizing: border-box;
}

button {
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}
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
