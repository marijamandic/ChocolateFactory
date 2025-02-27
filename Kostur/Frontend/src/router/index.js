import { createRouter, createWebHashHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';

const routes = [
  {
    path: "/",
    name: "home",
    component: HomeView,
  },
  {
    path: "/about",
    name: "about",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/AboutView.vue"),
  },
  {
    path: '/factoryInfo/:id', // Dodajemo dinamički segment za ID fabrike
    name: 'FactoryInfo',
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/FactoryInfo.vue"),
  },
  {
    path: '/chocolates', // Dodajemo dinamički segment za ID fabrike
    name: 'Chocolates',
    // component: () =>
    //   import(/* webpackChunkName: "about" */ "../views/FactoryInfo.vue"),
  },
  {
    path: '/registration', // Dodajemo dinamički segment za ID fabrike
    name: 'Registration',
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/Registration.vue"),
  },
  {
    path: '/login', // Dodajemo dinamički segment za ID fabrike
    name: 'Login',
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/Login.vue"),
  },
  {
    path: '/profile/:username', // Dodajemo dinamički segment za ID fabrike
    name: 'Profile',
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/Profile.vue"),
  },
  {
    path: '/shoppingCart/:shoppingCartId', // Dodajemo dinamički segment za ID fabrike
    name: 'ShoppingCart',
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/ShoppingCart.vue"),
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
