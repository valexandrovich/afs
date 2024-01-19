<script setup>

import {onMounted, reactive} from "vue";
import axios from "axios";

const state = reactive({
  jobs: []
})

onMounted(()=>{
  setInterval(fetchJobs, 500);
})

const fetchJobs = () => {
  console.log('fetching jobs')
  axios.get('/api/scheduler/jobs')
      .then(resp => {
        state.jobs = resp.data
      })
      .catch(err => {
        console.log(err)
      })
}

</script>

<template>
<h1>Progress</h1>



  <div class="flex flex-col">
    <div class="flex flex-row" v-for="job in state.jobs" :key="job.id">
      <span>{{job.id}} - {{job.storedJob.name}}   </span>

      <div class="bg-blue-100" v-for="step in job.steps" :key="step.id">
    {{step.id}} - {{step.status}} -{{step.progress}} -{{step.comment}} -
      </div>

    </div>
  </div>




</template>

<style scoped>

</style>