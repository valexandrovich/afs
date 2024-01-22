<script setup>
import {routes} from "@/router/routes.js";
</script>

<template>
  <aside class="flex flex-col custom-scrollbar from-green-500 text-white  to-green-900 w-64 bg-gradient-to-b  overflow-auto">
    <span class="text-5xl font-extrabold text-gray-100 text-center mt-4">
      <font-awesome-icon :icon="['fas', 'user-secret']"  />
      AFS
    </span>
    <hr class="my-5 mx-5 border-t border-2 border-green-600">

<!--    <span class="text-center   mb-4 font-bold   text-sm text-gray-100 ">cистема моніторингу та перевірок</span>-->
    <nav>
      <ul class="flex flex-col gap-2">
        <router-link v-for="route in routes" :key="route.name" :to="route.path"
        class="ml-6 px-4 py-2 text-lg font-semibold rounded-l-xl hover:bg-green-100  hover:text-green-700 transition-all duration-100">
          <font-awesome-icon :icon="route.icon" class="mr-2 fa-fw"/>
          {{ route.label }}
        </router-link>
      </ul>
    </nav>
  </aside>
</template>

<style scoped>

.router-link-active {
  @apply bg-gradient-to-r from-green-100 to-white text-green-700 shadow-xl;
}



.router-link-active:hover {
  @apply bg-white text-green-700 !important;
}

</style>