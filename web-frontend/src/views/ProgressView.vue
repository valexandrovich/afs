<script setup>

import {onMounted, reactive} from "vue";
import axios from "axios";

const state = reactive({
  jobs: []
})

onMounted(()=>{
  setInterval(fetchJobs, 500);
})

const formatDate = (timestamp) => {

  const date = new Date(timestamp);
  const day = date.getDate().toString().padStart(2, '0');
  const month = (date.getMonth() + 1).toString().padStart(2, '0'); // Note: months are zero-based
  const year = date.getFullYear();
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');
  const seconds = date.getSeconds().toString().padStart(2, '0');
  if (year == '1970'){
    return ''
  }
  return `${day}.${month}.${year} ${hours}:${minutes}:${seconds}`;
}

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

  <table>
    <thead>
    <tr class="bg-blue-300">
      <th>ID задачі</th>
      <th>Назва задачі</th>
      <th colspan="2">Опис задачі</th>
      <th>Час запуску</th>
      <!--        <th colspan="1"></th>-->
    </tr>
    </thead>
    <tbody>
    <template v-for="job in state.jobs" :key="job.id">
      <!-- Job Row -->
      <tr  class="bg-blue-200">
        <td>{{ job.id }}</td>
        <td>{{ job.storedJob.name }}</td>
        <td colspan="2">{{ job.storedJob.description }}</td>
        <td>{{ formatDate(job.startedAt) }}</td>
        <!--          <td colspan="1"></td>-->
      </tr>
      <!-- Sub Rows for Steps -->
      <tr class=" bg-gray-200">
        <th>ID Кроку</th>
        <th>Сервіс</th>
        <th>Прогрес</th>
        <th>Статус</th>
        <th>Коментар</th>
      </tr>
      <tr v-for="step in job.steps" :key="step.id" class=" bg-gray-100">
        <td>{{step.id}}</td> <!-- Empty cell for alignment -->
        <td>{{step.storedStep.serviceName}}</td> <!-- Empty cell for alignment -->
        <td class="font-semibold">{{ Number((step.progress * 100).toFixed(2))}} %</td> <!-- Empty cell for alignment -->

        <td v-if="step.status==='FINISHED'" class="text-green-700 font-semibold">Завершено</td>
        <td v-if="step.status==='IN_PROCESS'" class="text-blue-500 font-semibold">Обробка</td>
        <td v-if="step.status==='FAILED'" class="text-red-500 font-semibold">Помилка</td>
        <td>{{ step.comment }}</td>
      </tr>
    </template>
    </tbody>
  </table>



<!--  <div class="flex flex-col">-->
<!--    <div class="flex flex-row" v-for="job in state.jobs" :key="job.id">-->
<!--      <span>{{job.id}} - {{job.storedJob.name}}   </span>-->

<!--      <div class="bg-blue-100" v-for="step in job.steps" :key="step.id">-->
<!--    {{step.id}} - {{step.status}} -{{  (step.progress * 100).toFixed(2)}}% -{{step.comment}} - -->
<!--      </div>-->

<!--    </div>-->
<!--  </div>-->




</template>

<style scoped>

</style>