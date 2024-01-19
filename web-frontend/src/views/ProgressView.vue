<script setup>

import { computed, onMounted, reactive } from 'vue'
import axios from 'axios'

const state = reactive({
  jobs: [],
  isCollapse: true,
  // openedJobs: [2, 5],
  jobsVisibility: {
  }

})


const sortedJobs = computed(()=>{
  return [...state.jobs].sort((a, b) => {
    // Convert startedAt to Date objects for accurate comparison
    const dateA = new Date(a.startedAt);
    const dateB = new Date(b.startedAt);

    // Compare the two dates for descending order
    return dateB - dateA;
  });
})

onMounted(() => {
  setInterval(fetchJobs, 500)
})

const formatDate = (timestamp) => {

  const date = new Date(timestamp)
  const day = date.getDate().toString().padStart(2, '0')
  const month = (date.getMonth() + 1).toString().padStart(2, '0') // Note: months are zero-based
  const year = date.getFullYear()
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  const seconds = date.getSeconds().toString().padStart(2, '0')
  if (year == '1970') {
    return ''
  }
  return `${day}.${month}.${year} ${hours}:${minutes}:${seconds}`
}

const changeJobStepsVisibility = (jobId) => {
  if (state.jobsVisibility[jobId]){
    state.jobsVisibility[jobId] = false
  } else if (!state.jobsVisibility[2]){
    state.jobsVisibility[jobId] = true
  } else {
    state.jobsVisibility[jobId] = true
  }
}



const fetchJobs = () => {
  // console.log('fetching jobs')
  axios.get('/api/scheduler/jobs')
    .then(resp => {
      state.jobs = resp.data

      const inProcessJobIds = state.jobs
        .filter(job => job.steps.some(step => step.status === 'IN_PROCESS'))
        .map(job => job.id);

      inProcessJobIds.forEach(j => {
        if (state.jobsVisibility[j] === undefined){
          state.jobsVisibility[j] = true
        }
      })




    })
    .catch(err => {
      console.log(err)
    })
}

</script>

<template>
  <h1>Progress</h1>


  <div class="p-4">
  <div class="flex flex-row bg-blue-600 text-white text-xl rounded-2xl py-2">
    <div class="flex flex-col w-5p text-center">
      <span>Деталі</span>
    </div>
    <div class="flex flex-col w-10p text-center">
      <span>ID задачі</span>
    </div>
    <div class="flex flex-col w-25p">
      <span>Назва задачі</span>
    </div>
    <div class="flex flex-col w-30p">
      <span>Опис задачі</span>
    </div>
    <div class="flex flex-col w-10p text-center">
      <span>Час запуску</span>
    </div>

    <div class="flex flex-col w-10p text-center">
      <span>Час закінчення</span>
    </div>

    <div class="flex flex-col w-15p text-center">
      <span>Оператор</span>
    </div>
  </div>


<!--    {{state.jobs}}-->

  <template v-for="(job, index) in sortedJobs" :key="job.id">
    <div class="flex flex-row py-2 cursor-pointer my-2  rounded-2xl hover:bg-blue-300" @click="changeJobStepsVisibility(job.id)"
:class="index % 2 == 0 ? 'bg-blue-200': 'bg-blue-100' ">
      <!--         :class="{-->
<!--         'text-red-700': job.steps.some(step => step.status === 'FAILED'),-->
<!--          'bg-blue-50': job.steps.some(step => step.status === 'IN_PROCESS'),-->
<!--          'bg-green-50': job.steps.every(step => step.status === 'FINISHED' || step.status === 'SKIPPED')}"-->
<!--    >-->
      <div class="flex flex-col w-5p  text-center justify-center">
        <span class="text-blue-400 font-extrabold">{{state.jobsVisibility[job.id] ? 'V' : '>'}}</span>
      </div>
      <div class="flex flex-col w-10p  text-center justify-center">
        <span>  {{ job.id }}</span>
      </div>
      <div class="flex flex-col w-25p  justify-center ">
        <span>{{ job.storedJob.name }}</span>
      </div>
      <div class="flex flex-col w-30p  justify-center">
        <span>{{ job.storedJob.description }}</span>
      </div>
      <div class="flex flex-col w-10p  text-center">
        <span>{{ job.startedAt == null ? 'невідомий' : formatDate(job.startedAt) }}</span>
      </div>

      <div class="flex flex-col w-10p  text-center">
        <span>{{ job.finishedAt == null ? 'невідомий' : formatDate(job.finishedAt) }}</span>
      </div>

      <div class="flex flex-col w-15p    justify-center text-center">
        <span class="">{{ job.initiatorName }}</span>
      </div>
    </div>
    <div class="flex flex-col  ml-24 gap-2 " v-show="state.jobsVisibility[job.id]">
      <div class="flex flex-row bg-gray-600 text-white rounded-xl p-1">
        <div class="flex flex-col w-10p text-center">ID кроку</div>
        <div class="flex flex-col w-10p">Сервіс</div>
        <div class="flex flex-col w-10p text-center">Прогресс</div>
        <div class="flex flex-col w-10p text-center">Чвс старту</div>
        <div class="flex flex-col w-10p text-center">Час завершення</div>
        <div class="flex flex-col w-10p text-center">Статус</div>
        <div class="flex flex-col w-40p">Коментар</div>
      </div>
      <div class="flex flex-row bg-gray-200 rounded-xl p-1" v-for="step in job.steps" :key="step.id">
        <div class="flex flex-col w-10p text-center">{{ step.id }}</div>
        <div class="flex flex-col w-10p">{{ step.storedStep.serviceName }}</div>
        <div class="flex flex-col w-10p text-center">{{ Number((step.progress * 100).toFixed(2)) }} %</div>
        <div class="flex flex-col w-10p text-center">{{ step.startedAt == null ? 'невідомий' : formatDate(step.startedAt) }}</div>
        <div class="flex flex-col w-10p text-center">{{ step.finishedAt == null ? 'невідомий' : formatDate(step.finishedAt) }}</div>
        <div v-if="step.status==='FINISHED'" class="flex flex-col text-green-700 font-semibold  w-10p text-center">
          Завершено
        </div>
        <div v-if="step.status==='IN_PROCESS'" class="flex flex-col text-blue-500 font-semibold   w-10p text-center">
          Обробка
        </div>
        <div v-if="step.status==='FAILED'" class="flex flex-col text-red-500 font-semibold   w-10p text-center">Помилка
        </div>
        <div v-if="step.status==='SKIPED'" class="flex flex-col text-amber-600 font-semibold   w-10p text-center">Пропущено
        </div>
        <div class="flex flex-col w-40p">{{ step.comment }}</div>
      </div>
    </div>
  </template>

  </div>



</template>

<style scoped>


</style>