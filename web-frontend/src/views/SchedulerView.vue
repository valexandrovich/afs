<script setup>


import {onMounted, reactive} from "vue";
import axios from "axios";


const state = reactive({
  storedJobs: []
});

onMounted(() => {
  axios.get('/api/scheduler/stored-jobs')
      .then(resp => {
        state.storedJobs = resp.data
      })
      .catch(err => {
        console.log(err)
      })
})

</script>

<template>
<h1>Scheduler</h1>

  {{state.storedJobs}}

</template>

<style scoped>

</style>