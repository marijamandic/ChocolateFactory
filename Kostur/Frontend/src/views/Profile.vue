<template>
  <div class="profile-page">
    <div class="container">

      <!-- Informacije o korisniku -->
      <div class="left-column">
        <div class="profile-info">
          <p>            
            <span class="material-icons profile-icon">account_circle</span>
          </p>
          <h3>{{ user?.name }} {{ user?.surname }}</h3>
          <p> @{{ user?.username }}</p>
          <p> {{ user?.role }}</p>
          <button  @click="editUser(user.id)" >Edit Profile</button>
        </div>
      </div>

      <!-- CUSTOMER -->
      <div class="right-column" v-if="user?.role === 'CUSTOMER'">
        <div class="image-section">
          <img :src="getCustomerTypeImagePath(user?.customerType.type)" :alt="user.alt">
        </div>
    
        <div class="text-section">
          <h1>You are a {{user.customerType.type}} customer!</h1>
          <p>Your score is {{user.score}}</p>
        </div>
    
        <div class="bottom-text" v-if="user.customerType.type !== 'GOLD'">
          <p>You need {{ user.customerType.score - user.score }} more points to upgrade your status</p>
        </div>
      </div>

      <!-- ADMIN -->
      <div class="right-column" v-if="user?.role === 'ADMIN'">

      <div class="users-list">
        <h3>Users</h3>
        <table v-if="allUsers.length > 0" class="table-container">
          <table class="users-table">
            <tr>
              <td>Username</td>
              <td>Name</td>
              <td>Surname</td>
              <td>Gender</td>
              <td>Role</td>
            </tr>
            <tr v-for="(user, index) in allUsers" :key="user.id" 
              :class="{'light-row': index % 2 === 0, 'dark-row': index % 2 !== 0}">
            <td>@{{ user.username }}</td>
            <td>{{ user.name }}</td>
            <td>{{ user.surname }}</td>
            <td>{{ getGender(user.gender) }}</td>
            <td>{{ getRole(user.role) }}</td> 
          </tr>
        </table>
      </table>
      </div>
      </div>
      
      <!-- MANAGER: Podaci o fabrici i cokoladama -->
      <div class="right-column" v-if="user?.role === 'MANAGER'">
        <div class="factory-info">
          <h2>{{ factory?.name }}</h2>
          <!-- <img :src="getFactoryImagePath(factory?.logo)"> -->
          <p>Location: {{ factory?.location.address }}</p>
        </div>
        
        <div class="chocolates-list">
          <h3>Chocolates</h3>
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
              <tr v-for="(chocolate, index) in chocolates" :key="chocolate.id" 
                :class="{'light-row': index % 2 === 0, 'dark-row': index % 2 !== 0}">
              <td>{{ chocolate.name }}</td>
              <td>{{ chocolate.description }}</td>
              <td>{{ getChocolateKind(chocolate) }}</td>
              <td>{{ chocolate.price }}</td>
              <td>{{ chocolate.quantity }}</td>
              <td>{{ getChocolateType(chocolate) }}</td>              
              <td>
                <div class="action-buttons">
                  <button 
                    @click="editChocolate(chocolate.id)" 
                    
                    :class="{'light-btn': index % 2 !== 0}"
                  >
                    Edit
                  </button>
                  <button 
                    @click="deleteChocolate(chocolate.id)" 
                    :class="{'light-btn': index % 2 !== 0}"
                  >
                    Delete
                  </button>
                </div>
              </td>
              
            </tr>
          </table>
        </table>
          <button @click="showModal = true">Add Chocolate</button>
        </div>
      </div>
    </div>
  </div>


  <!-- Dodavanje cokolade -->
  <div>

    <div v-if="showModal" class="modal-overlay">
      <div class="modal">
        <h2>Add New Chocolate</h2>

        <form @submit.prevent="addChocolate">
          <label>Name:</label>
          <input v-model="chocolate.name" type="text" required />

          <label>Price:</label>
          <input v-model="chocolate.price" type="number" required />

          <label>Type:</label>
          <select v-model="chocolate.type" required>
            <option value="ORDINARY">Ordinary</option>
            <option value="FOR_COOKING">For cooking</option>
            <option value="FOR_DRINKING">For drinking</option>
            <option value="GIFT">Gift</option>

          </select>

          <label>Kind:</label>
          <select v-model="chocolate.kind" required>
            <option value="DARK">Dark</option>
            <option value="MILK">Milk</option>
            <option value="WHITE">White</option>
            <option value="FLAVORED">Flavored</option>
          </select>

          <label>Weight (g):</label>
          <input v-model="chocolate.weight" type="number" required />

          <label>Description:</label>
          <textarea v-model="chocolate.description"></textarea>

          <label>Image:</label>
          <input type="file" @change="handleImageUpload" />

          <div class="buttons">
            <button type="submit" class="confirm-button">Add</button>
            <button type="button" class="cancel-button" @click="showModal = false">Cancel</button>
          </div>
        </form>
      </div>
    </div>
  </div>

  <!-- Edit cokolade -->
<div v-if="showEditModal" class="modal-overlay">
  <div class="modal">
    <h2>Edit Chocolate</h2>

    <form @submit.prevent="updateChocolate">
      <label>Name:</label>
      <input v-model="editedChocolate.name" type="text" required />

      <label>Price:</label>
      <input v-model="editedChocolate.price" type="number" required />

      <label>Type:</label>
      <select v-model="editedChocolate.type" required>
        <option value="ORDINARY">Ordinary</option>
        <option value="FOR_COOKING">For cooking</option>
        <option value="FOR_DRINKING">For drinking</option>
        <option value="GIFT">Gift</option>
      </select>

      <label>Kind:</label>
      <select v-model="editedChocolate.kind" required>
        <option value="DARK">Dark</option>
        <option value="MILK">Milk</option>
        <option value="WHITE">White</option>
        <option value="FLAVORED">Flavored</option>
      </select>

      <label>Weight (g):</label>
      <input v-model="editedChocolate.weight" type="number" required />

      <label>Description:</label>
      <textarea v-model="editedChocolate.description"></textarea>

      <label>Image:</label>
      <input type="file" @change="handleEditImageUpload" />

      <div class="buttons">
        <button type="submit" class="confirm-button">Save</button>
        <button type="button" class="cancel-button" @click="showEditModal = false">Cancel</button>
      </div>
    </form>
  </div>
</div>

 <!-- Edit profila -->
<div v-if="showEditProfileModal" class="modal-overlay">
  <div class="modal">
    <h2>Edit Profile</h2>

  <form @submit.prevent="updateUser">
    <div>
      <label for="username">Username:</label>
      <input type="text" id="username" v-model="editedUser.username" required />
    </div>

    <div>
      <label for="password">Password:</label>
      <input type="password" id="password" v-model="editedUser.password" required />
    </div>

    <div>
      <label for="firstName">Name:</label>
      <input type="text" id="firstName" v-model="editedUser.name" required />
    </div>

    <div>
      <label for="lastName">Surname:</label>
      <input type="text" id="lastName" v-model="editedUser.surname" required />
    </div>

    <div>
      <label for="gender">Pol:</label>
      <select id="gender" v-model="editedUser.gender" required>
        <option value="MALE">Male</option>
        <option value="FEMALE">Female</option>
      </select>
    </div>

    <div>
      <label for="dob">Birthday:</label>
      <input type="date" id="dob" v-model="editedUser.birthday" required />
    </div>

    <button type="submit">Done</button>
    <button type="button" @click="showEditProfileModal = false">Cancel</button>

  </form>
  </div>
</div>

</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRoute } from 'vue-router';
import { format } from 'date-fns';

const route = useRoute();
const username = route.params.username;
const user = ref(null);
const factory = ref(null);
const chocolates = ref([]);
const showModal = ref(false);
const chocolate = ref({
  name: '',
  price: '',
  type: '',
  category: '',
  weight: '',
  description: '',
  image: null
});
const showEditModal = ref(false);
const editedChocolate = ref({});
const showEditProfileModal = ref(false);
const editedUser = ref({});
const allUsers = ref([]);

onMounted(async () => {
  await fetchUser();
    await fetchAllUsers();
});

const getFactoryImagePath = (imageName) => `/assets/FactoryLogos/${imageName}`;
const getCustomerTypeImagePath = (imageName) => `/assets/CustomerTypes/${imageName}.png`;

async function fetchUser() {
  try {
    console.log("Fetching user for username:", username);
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/users/getByUsername/${username}`);
    console.log("User data:", response.data);
    user.value = response.data;

    if (user.value?.role === 'MANAGER' && user.value.factory) {
      console.log("Fetching factory for ID:", user.value.factory.id);
      await fetchFactory(user.value.factory.id);
      await fetchChocolates(user.value.factory.id);
    }
  } catch (error) {
    console.error('Error loading user: ', error);
  }
}

async function fetchAllUsers() {
  try {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/users`);
    allUsers.value = response.data;
  } catch (error) {
    console.error('Error fetching users:', error);
  }
}

async function fetchFactory(factoryId) {
  try {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/factories/${factoryId}`);
    factory.value = response.data;
  } catch (error) {
    console.error('Error loading factory: ', error);
  }
}

async function fetchChocolates(factoryId) {
  try {
    const chocolatesResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/getByFactory/${factoryId}`);
    chocolates.value = chocolatesResponse.data;
  } catch (error) {
    console.error('Error fetching chocolates:', error);
  }
}

function editChocolate(id) {
  const chocolateToEdit = chocolates.value.find(choco => choco.id === id);
  if (!chocolateToEdit) return;
  
  editedChocolate.value = { ...chocolateToEdit }; 
  showEditModal.value = true;
}

function handleEditImageUpload(event) {
  const file = event.target.files[0];
  editedChocolate.value.image = file;
}

async function updateChocolate() {
  try {
    const updatedChocolate = {
      ...editedChocolate.value,
      factory: { id: factory.value.id }, 
    };

    await axios.put(
      `http://localhost:8080/WebShopAppREST/rest/chocolates/${editedChocolate.value.id}`,
      updatedChocolate,
      { headers: { "Content-Type": "application/json" } }
    );

    const index = chocolates.value.findIndex(choco => choco.id === editedChocolate.value.id);
    if (index !== -1) {
      chocolates.value[index] = { ...editedChocolate.value };
    }

    alert("Chocolate updated successfully!");
    showEditModal.value = false;
  } catch (error) {
    console.error("Error updating chocolate:", error);
  }
}

function editUser(id) {
  editedUser.value = { ...user.value };
  showEditProfileModal.value = true;
}


async function updateUser() {
  console.log("PUT URL ID:", user.value.id);
  console.log("Body ID:", editedUser.value.id);

  try {
    await axios.put(
      `http://localhost:8080/WebShopAppREST/rest/users/${user.value.id}`,
      editedUser.value,
      { headers: { "Content-Type": "application/json" } }
    );

    Object.assign(user.value, editedUser.value); // Ažuriraj glavni objekat
    alert("User updated successfully!");
    showEditProfileModal.value = false;
  } catch (error) {
    console.error("Error updating user:", error);
  }
}

function handleImageUpload(event) {
  const file = event.target.files[0];
  if (file) {
    chocolate.value.image = file;
  }
}

async function addChocolate() {
  try {
    const chocolateData = {
      name: chocolate.value.name,
      price: chocolate.value.price,
      kind: chocolate.value.kind, 
      type: chocolate.value.type,
      weight: chocolate.value.weight,
      description: chocolate.value.description,
      quantity: chocolate.value.quantity,
      image: chocolate.value.image.name,
      factory: {
        id: factory.value.id 
      }

    };

    console.log("chocolate: ", chocolateData);

    const response = await axios.post(
      "http://localhost:8080/WebShopAppREST/rest/chocolates/add",
      chocolateData,
      { headers: { "Content-Type": "application/json" } }
    );

    alert("Chocolate added successfully!");
    showModal.value = false;
    await fetchChocolates(factory.value.id);
  } catch (error) {
    console.error("Error adding chocolate:", error);
  }
}

function deleteChocolate(chocolateId) {
  const isConfirmed = window.confirm("Are you sure you want to delete this chocolate?");
  if (!isConfirmed) return;

  axios.delete(`http://localhost:8080/WebShopAppREST/rest/chocolates/delete/${chocolateId}`)
    .then(() => {
      console.log("Chocolate deleted successfully.");
      chocolates.value = chocolates.value.filter(choco => choco.id !== chocolateId);
    })
    .catch(error => {
      console.error("Error deleting chocolate:", error);
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

function getGender(gender){
  let message;
  switch (gender){
    case "MALE":
      message = "Male";
      break;
    case "FEMALE":
      message = "Female";
      break;
    default:
      message = "Unknown gender";
  }
  return message;

}

function getRole(role){
  let message;
  switch (role){
    case "ADMIN":
      message = "Admin";
      break;
    case "MANAGER":
      message = "Manager";
      break;
    case "WORKER":
      message = "Worker";
      break;
    case "CUSTOMER":
      message = "Customer";
      break;
    default:
      message = "Unknown gender";
  }
  return message;

}

</script>

<style scoped>
.profile-page {
  display: flex;
}

.container {
  display: flex;
  width: 100%;
}

.left-column {
  display: flex;
  justify-content: left;
  width: 30%;
  padding: 20px;
  border-radius: 8px;
  color: rgb(129, 70, 41);
}

.right-column {
  width: 70%;
  padding: 20px;
  color: rgb(129, 70, 41);
}

.profile-info {
  width: 70%;
  margin-bottom: 20px;
  border-right: 2px solid rgb(210, 160, 120);
  height: 100%;
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

button {
  padding: 10px 20px;
  background-color: rgb(210, 160, 120);
  color: white;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  width: auto;
  border-radius: 15px;
  margin-top: 15px;
  margin-right: 10px;
  margin-left: 10px;
  border: 2px solid rgb(210, 160, 120);
}

button:hover {
  background-color: rgb(129, 70, 41);
}

.profile-icon {
  font-size: 100px;
  margin-left: auto;
  vertical-align: middle;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin: 0 auto;
  padding: 10px;
  width: 500px;
  border: 1px solid rgb(129, 70, 41);
  border-radius: 8px;
  background-color: rgb(210, 160, 120);
  max-height: 90vh;
  overflow-y: auto;
}

.modal h2{
  text-align: center;
  display: block;
  margin-bottom: 15px;
  color: white;
  font-weight: bold;
  font-size: 25px;
}

.modal div {
  margin-bottom: 10px;
}

.modal label {
  display: block;
  margin-bottom: 5px;
  color: white;
  font-size: 15px;
  font-weight: bold;
}

.modal input,
.modal select {
  width: 300px;
  padding: 8px;
  box-sizing: border-box;
  border-radius: 15px;
}

.modal button {
  padding: 10px 20px;
  background-color: rgb(129, 70, 41);
  color: white;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  width: auto;
  border-radius: 15px;
  margin-top: 15px;
  border: 2px solid rgb(129, 70, 41);
}

input, select, textarea {
  width: 100%;
  margin-bottom: 10px;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.buttons {
  display: flex;
  justify-content: space-between;
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
  margin: auto;
  font-family: 'Times New Roman', Times, serif;
  text-align: center;
  color: rgb(210, 160, 120);
  width: 100%;
  margin-bottom: 10px;
  margin-top: 40px
}

.chocolates-table {
  width: 80%;
  margin: auto;
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

.users-table {
  width: 800px;
  margin: auto;
  border-collapse: separate;
  border-spacing: 0;
  border-radius: 15px;
  overflow: hidden;
  justify-content: center;
}
.users-table th, .users-table td {
  padding: 8px;
  height: 40px;
  text-align: left;
}
.users-table th {
  background-color: rgb(230, 200, 180);
}

.light-row {
  background-color: rgba(210, 161, 120, 0.119);
}

.dark-row {
  background-color: rgb(210, 160, 120);
}

.light-btn{
  padding: 10px 20px;
  background-color: rgb(210, 160, 120);
  color: white;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  width: auto;
  border-radius: 15px;
  margin-top: 15px;
  margin-right: 10px;
  margin-left: 10px;
  border: 2px solid rgb(210, 160, 120);
}

.light-btn:hover {
  background-color: rgb(129, 70, 41);
}

.action-buttons {
  display: flex;
  gap: 10px;
  justify-content: center;
}
</style>
