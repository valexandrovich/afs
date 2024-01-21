<script setup>
import {routes} from "@/router/routes.js";
</script>

<template>
  <aside class="flex flex-col custom-scrollbar from-blue-500 text-white  to-blue-900 w-64 bg-gradient-to-b  overflow-auto">
    <span class="text-5xl font-extrabold text-gray-100 text-center mt-4">
      <font-awesome-icon :icon="['fas', 'user-secret']"  />
      AFS
    </span>
    <hr class="my-5 mx-5 border-t border-2 border-blue-400">

<!--    <span class="text-center   mb-4 font-bold   text-sm text-gray-100 ">cистема моніторингу та перевірок</span>-->
    <nav>
      <ul class="flex flex-col gap-2">
        <router-link v-for="route in routes" :key="route.name" :to="route.path"
        class="ml-6 px-4 py-2 text-lg font-semibold rounded-l-xl hover:bg-white  hover:text-blue-500 transition-all duration-100">
          <font-awesome-icon :icon="route.icon" class="mr-2 fa-fw"/>
          {{ route.label }}
        </router-link>
      </ul>
    </nav>
  </aside>
</template>

<style scoped>

.router-link-active {
  @apply bg-white text-blue-500 shadow-xl;
}



.router-link-active:hover {
  @apply bg-white text-blue-500 !important;
}

</style>