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
  <div class="flex flex-row bg-blue-500 text-blue-50 text-lg font-bold   rounded-t-2xl py-2">
    <div class="flex flex-col w-2p text-center">
<!--      <span>Деталі</span>-->
    </div>
    <div class="flex flex-col w-10p text-center">
      <span>ID</span>
    </div>
    <div class="flex flex-col w-45p">
      <span>Назва</span>
    </div>
<!--    <div class="flex flex-col w-30p">-->
<!--      <span>Опис</span>-->
<!--    </div>-->
    <div class="flex flex-col w-15p text-center">
      <span>Старт</span>
    </div>

    <div class="flex flex-col w-15p text-center">
      <span>Фініш</span>
    </div>

    <div class="flex flex-col w-15p text-center">
      <span>Оператор</span>
    </div>

    <div class="flex flex-col w-15p text-center">
      <span>Статус</span>
    </div>
  </div>


  <div class="flex w-full bg-gray-100 rounded-b-2xl  justify-center py-12" v-if="state.jobs.length == 0">
  <div class="loader justify-center text-center " ></div>
  </div>
  <template v-else v-for="(job, index) in sortedJobs" :key="job.id" >
    <div class="flex flex-row py-1 cursor-pointer text-sm  text-gray-600 font-semibold  hover:bg-blue-200" @click="changeJobStepsVisibility(job.id)"
         :class="index % 2 == 0 ? 'bg-blue-50': 'bg-blue-100' ">
      <!--         :class="{-->
      <!--         'text-red-700': job.steps.some(step => step.status === 'FAILED'),-->
      <!--          'bg-blue-50': job.steps.some(step => step.status === 'IN_PROCESS'),-->
      <!--          'bg-green-50': job.steps.every(step => step.status === 'FINISHED' || step.status === 'SKIPPED')}"-->
      <!--    >-->
      <div class="flex flex-col w-2p  text-end justify-center text-blue-400">
        <span v-if="state.jobsVisibility[job.id]"><font-awesome-icon :icon="['fas', 'chevron-down']" class="fa-fw"/></span>
        <span v-else><font-awesome-icon :icon="['fas', 'chevron-right']" class="fa-fw"/></span>

<!--        <span class="text-blue-400 font-extrabold">-->
<!--          {{state.jobsVisibility[job.id] ? '' : '>'}}-->

<!--        </span>-->
      </div>
      <div class="flex flex-col w-10p  text-center  justify-center">
        <span>  {{ job.id }}</span>
      </div>
      <div class="flex flex-col w-45p  justify-center ">
        <span>{{ job.storedJob.name }}</span>
      </div>
<!--      <div class="flex flex-col w-30p  justify-center">-->
<!--        <span>{{ job.storedJob.description }}</span>-->
<!--      </div>-->
      <div class="flex flex-col w-15p  text-center">
        <span>{{ job.startedAt == null ? 'невідомий' : formatDate(job.startedAt) }}</span>
      </div>

      <div class="flex flex-col w-15p  text-center ">
        <span>{{ job.finishedAt == null ? 'невідомий' : formatDate(job.finishedAt) }}</span>
      </div>

      <div class="flex flex-col w-15p    justify-center text-center">
        <span class="">{{ job.initiatorName }}</span>
      </div>


      <!--         'text-red-700': job.steps.some(step => step.status === 'FAILED'),-->
      <!--          'bg-blue-50': job.steps.some(step => step.status === 'IN_PROCESS'),-->
      <!--          'bg-green-50': job.steps.every(step => step.status === 'FINISHED' || step.status === 'SKIPPED')}"-->

      <div class="flex flex-col w-15p    justify-center ">
          <span v-if="job.steps.some(step => step.status === 'FAILED')" class="text-red-400"><font-awesome-icon :icon="['fas', 'circle-exclamation']" class="mr-2 fa-fw"/> Помилка</span>
        <span v-if="job.steps.some(step => step.status === 'IN_PROCESS')" class="text-blue-400"><font-awesome-icon :icon="['fas', 'spinner']" class="mr-2 fa-fw"/> Обробка</span>
        <span v-if="job.steps.every(step => step.status === 'FINISHED' || step.status === 'SKIPPED')" class="text-green-600"><font-awesome-icon :icon="['fas', 'circle-check']" class="mr-2 fa-fw"/> Завершено</span>
      </div>
    </div>
    <div class="flex flex-col  text-sm  mx-24 " v-show="state.jobsVisibility[job.id]">
      <div class="flex flex-row bg-blue-400 text-gray-50 font-semibold  gap-2">
        <div class="flex flex-col w-5p text-center">ID </div>
        <div class="flex flex-col w-10p">Сервіс</div>
        <div class="flex flex-col w-10p text-center">Прогресс</div>
        <div class="flex flex-col w-15p text-center">Старт</div>
        <div class="flex flex-col w-15p text-center">Фініш</div>
        <div class="flex flex-col w-15p text-center">Статус</div>
        <div class="flex flex-col w-30p">Коментар</div>
      </div>
      <div class="flex flex-row py-0.5 bg-gray-100 text-sm text-gray-600  gap-2" v-for="step in job.steps" :key="step.id">
        <div class="flex flex-col w-5p text-center">{{ step.id }}</div>
        <div class="flex flex-col w-10p">{{ step.storedStep.serviceName }}</div>
        <div class="flex flex-col w-10p text-center">{{ Number((step.progress * 100).toFixed(2)) }} %</div>
        <div class="flex flex-col w-15p text-center">{{ step.startedAt == null ? 'невідомий' : formatDate(step.startedAt) }}</div>
        <div class="flex flex-col w-15p text-center">{{ step.finishedAt == null ? 'невідомий' : formatDate(step.finishedAt) }}</div>
        <div v-if="step.status==='FINISHED'" class="flex flex-col text-green-700 font-semibold  w-15p text-center">
         <span>  <font-awesome-icon :icon="['fas', 'circle-check']" class="mr-2 fa-fw"/>Завершено</span>
        </div>
        <div v-if="step.status==='IN_PROCESS'" class="flex flex-col text-blue-500 font-semibold   w-10p text-center">
          <span><font-awesome-icon :icon="['fas', 'spinner']" class="mr-2 fa-fw"/> Обробка</span>
        </div>
        <div v-if="step.status==='FAILED'" class="flex flex-col text-red-500 font-semibold   w-10p text-center">
          <span> <font-awesome-icon :icon="['fas', 'circle-exclamation']" class="mr-2 fa-fw"/>Помилка</span>
        </div>
        <div v-if="step.status==='SKIPED'" class="flex flex-col text-amber-600 font-semibold   w-10p text-center">Пропущено
        </div>
        <div class="flex flex-col w-30p">{{ step.comment }}</div>
      </div>
    </div>
  </template>

</template>

<style scoped>
.loader {
  border: 5px solid #f3f3f3; /* Light grey background */
  border-top: 5px solid #3498db; /* Blue color */
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 2s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>