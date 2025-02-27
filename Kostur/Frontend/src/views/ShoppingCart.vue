<template>
    <div class="chocolates">
        <h2>My shopping cart</h2>
        <h3>Chocolates:</h3>
        <table v-if="chocolates.length > 0" class="table-container">
          <table class="chocolates-table">
            <tr>
              <td></td>
              <td>Name</td>
              <td>Weight</td>
              <td>Kind</td>
              <td>Price</td>
              <td>Type</td>
              <td>Quantity</td>
            </tr>
            <tr v-for="(chocolate, index) in chocolates" :key="chocolate.uuid" 
                :class="{'light-row': index % 2 === 0, 'dark-row': index % 2 !== 0}">
              <td>
                <img :src="getChocolateImagePath(chocolate.image)" class="image" :alt="chocolate.alt">
              </td>
              <td>{{ chocolate.name }}</td>
              <td>{{ chocolate.description }}</td>
              <td>{{ getChocolateKind(chocolate) }}</td>
              <td>{{ chocolate.price }}</td>
              <td>{{ getChocolateType(chocolate) }}</td>
              <td>{{ chocolate.quantityInCart }}</td>
              
              <td><button @click="removeMultipleFromCart(chocolate)"
                      :disabled="!chocolate.quantityToRemove || chocolate.quantityToRemove <= 0">Remove</button>
              </td>
              <td><input class="quantity-input" type="number" 
                min="0" :max="chocolate.quantityInCart" v-model.number="chocolate.quantityToRemove"></td>
            </tr>
          </table>
        </table>
        <h3 v-if="!shoppingCart">There is no chocolates in cart</h3>
      </div>
    <h3 v-if="shoppingCart">Total: {{shoppingCart.price}}</h3>
    <div class="button-container">
      <button @click="buy" class="buy-button">Buy</button>
    </div>
</template>

<script setup>
import axios from 'axios';
import { onMounted, ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const route = useRoute();
const chocolates = ref([]);
const shoppingCart = ref(null);
const shopping = ref(null);


onMounted(() => {
    fetchShoppingCart();
});

const getChocolateImagePath = (imageName) => `/assets/Chocolates/${imageName}`;

function fetchShoppingCart() {
  const shoppingCartId = route.params.shoppingCartId;
  axios.get(`http://localhost:8080/WebShopAppREST/rest/shoppingCarts/${shoppingCartId}`)
    .then(response => {
      shoppingCart.value = response.data;
      console.log("shopping cart: ", shoppingCart.value);

      if (shoppingCart.value && Array.isArray(shoppingCart.value.chocolates)) {
        const groupedChocolates = shoppingCart.value.chocolates.reduce((acc, chocolate) => {
          const existingChocolate = acc.find(choco => choco.id === chocolate.id);
          if (existingChocolate) {
            existingChocolate.quantityInCart += 1;
          } else {
            acc.push({ ...chocolate, quantityInCart: 1 });
          }
          return acc;
        }, []);
        
        chocolates.value = groupedChocolates;
      } else {
        console.error("No chocolates found in the shopping cart or invalid format.");
      }
    })
    .catch(error => {
      console.error('Error loading shoppingCart:', error);
      alert('Došlo je do greške prilikom učitavanja korpe.');
    });
}

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

async function removeMultipleFromCart(chocolate) {
  if (chocolate.quantityToRemove > 0) {
    for (let i = 0; i < chocolate.quantityToRemove; i++) {
      await removeFromCart(chocolate.id);
    }
  }
  window.location.reload();
}
async function removeFromCart(chocolateId) {
  try {
    const response = await axios.put(`http://localhost:8080/WebShopAppREST/rest/shoppingCarts/remove/${shoppingCart.value.id}/${chocolateId}`);
    shoppingCart.value = response.data;

    const index = chocolates.value.findIndex(chocolate => chocolate.id === chocolateId);
    if (index !== -1) {
      chocolates.value.splice(index, 1);
    }

    console.log("Updated shopping cart: ", shoppingCart.value);
  } catch (error) {
    console.error("Error removing from cart:", error);
    return false;
  }
}


async function buy() {
  if (shoppingCart.value.chocolates && shoppingCart.value.chocolates.length > 0) {
    const factoryId = shoppingCart.value.chocolates[0].factory.id;
    const date = new Date(); // Uzima trenutni datum i vreme
    const formattedDate = date.toISOString().slice(0, 16); // Uzimamo samo "yyyy-MM-dd'T'HH:mm"

    console.log(formattedDate); // Na primer, "2025-02-24T13:45"

    
    const orderData = {
      factory: {
        id: factoryId
      },
      shoppingCart: {
        id: shoppingCart.value.id 
      },
      shoppingDateTime: formattedDate,
      status: "PROCESSING"
    };

    try {
      const response = await axios.post('http://localhost:8080/WebShopAppREST/rest/shoppings/add', orderData);
      
      console.log("Order placed successfully:", response.data);

      localStorage.removeItem('shoppingCartId');
      alert('Kupovina je uspešno obavljena!');

    } catch (error) {
      console.error("Error placing order:", error);
      alert('Došlo je do greške prilikom obavljanja kupovine.');
    }
  } else {
    alert('Korpa je prazna!');
  }
}

function formatDate(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  
  return `${year}-${month}-${day} ${hours}:${minutes}`;
}

</script>

<style>

.container {
    margin: 20px;
    text-align: center;
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

.table-container {
    display: flex;
    justify-content: center;
    font-family: 'Open Sans', sans-serif;
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
  
.chocolates-table {
    width: 80%;
    border-collapse: separate;
    border-spacing: 0 10px;
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

.chocolates-table tr {
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1); 
  border-radius: 15px;
  overflow: hidden;
}

.chocolates-table tr td {
  padding: 10px;
  background-color: inherit;
}

.chocolates-table tr:first-child {
  font-weight: bold;
  border-radius: 0;
  box-shadow: none;
}

.chocolates-table tr td:first-child {
  border-top-left-radius: 15px;
  border-bottom-left-radius: 15px;
}

.chocolates-table tr td:last-child {
  border-top-right-radius: 15px;
  border-bottom-right-radius: 15px;
}

.light-row {
    background-color: rgba(210, 161, 120, 0.119);
}

.dark-row {
    background-color: rgb(210, 160, 120);
}

.image{
  width: 200px;
}

.quantity-input{
  width: 35px;
  height: 35px;
}

.buy-button {
  padding: 10px 20px;
  background-color: white;
  color: rgb(129, 70, 41);
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  border: 2px solid rgb(129, 70, 41);
  }
  
.buy-button:hover {
  background-color: rgb(210, 160, 120);
  color:white;
  }

.button-container {
  display: flex;
  justify-content: center;
}
</style>