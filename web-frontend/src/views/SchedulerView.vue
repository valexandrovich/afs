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

const initStoredJob = (storedJobId) => {

  console.log(typeof storedJobId)

  const storedJobRequest = {
    storedJobId: storedJobId,
    initiatorName: 'valex'
  }
  axios.post('/api/scheduler/init', storedJobRequest);
}

</script>

<template>
<h1 class="bg-amber-200">Scheduler</h1>


  <div class="flex flex-col">
    <div class="flex flex-row gap-4" v-for="job in state.storedJobs" :key="job.id">
      <span>{{job.id}} - {{job.name}} - {{job.description}}</span>
      <button @click="initStoredJob(job.id)">Start</button>
    </div>

  </div>

</template>

<style scoped>

</style>