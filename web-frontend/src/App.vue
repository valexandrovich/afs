<script setup>



import SideMenuComponent from "@/components/SideMenuComponent.vue";
</script>

<template>

  <div class="flex h-screen bg-gray-200">

    <SideMenuComponent/>

    <main class="flex-1 overflow-auto  p-8 bg-white  custom-scrollbar">
      <RouterView/>
    </main>


  </div>

<!--  <ul>-->
<!--    <router-link to="/">Home</router-link>-->
<!--    <router-link to="/scheduler">Scheduler</router-link>-->
<!--    <router-link to="/progress">Progress</router-link>-->
<!--  </ul>-->

<!--  <RouterView/>-->
</template>

<style scoped>

</style>
