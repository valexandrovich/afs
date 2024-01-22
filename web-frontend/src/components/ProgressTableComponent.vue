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
  <div class="flex flex-row bg-gradient-to-b from-green-500 to-green-700  text-green-50 text-lg font-bold   rounded-t-2xl py-1">
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
    <div class="flex flex-row py-1 cursor-pointer text-sm   text-gray-600 font-semibold  hover:bg-gray-300 " @click="changeJobStepsVisibility(job.id)"
         :class="[index % 2 == 0 ? 'bg-gray-100': 'bg-gray-200', index === sortedJobs.length - 1 ? 'rounded-b-2xl' : '']">
      <!--         :class="{-->
      <!--         'text-red-700': job.steps.some(step => step.status === 'FAILED'),-->
      <!--          'bg-green-50': job.steps.some(step => step.status === 'IN_PROCESS'),-->
      <!--          'bg-green-50': job.steps.every(step => step.status === 'FINISHED' || step.status === 'SKIPPED')}"-->
      <!--    >-->
      <div class="flex flex-col w-2p  text-end justify-center text-green-700">
        <span v-if="state.jobsVisibility[job.id]"><font-awesome-icon :icon="['fas', 'chevron-down']" class="fa-fw"/></span>
        <span v-else><font-awesome-icon :icon="['fas', 'chevron-right']" class="fa-fw"/></span>

<!--        <span class="text-green-400 font-extrabold">-->
<!--          {{state.jobsVisibility[job.id] ? '' : '>'}}-->

<!--        </span>-->
      </div>
      <div class="flex flex-col w-10p  text-center  justify-center">
        <span>  {{ job.id }}</span>
      </div>
      <div class="flex flex-col w-45p  justify-center ">
        <span>{{ job.storedJob.description }}</span>
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
      <!--          'bg-green-50': job.steps.some(step => step.status === 'IN_PROCESS'),-->
      <!--          'bg-green-50': job.steps.every(step => step.status === 'FINISHED' || step.status === 'SKIPPED')}"-->

      <div class="flex flex-col w-15p    justify-center ">
          <span v-if="job.steps.some(step => step.status === 'FAILED')" class="text-red-500"><font-awesome-icon :icon="['fas', 'circle-exclamation']" class="mr-2 fa-fw"/> Помилка</span>
        <span v-if="job.steps.some(step => step.status === 'IN_PROCESS')" class="text-blue-400"><font-awesome-icon :icon="['fas', 'spinner']" class="mr-2 fa-fw"/> Обробка</span>
        <span v-if="job.steps.every(step => step.status === 'FINISHED' || step.status === 'SKIPPED')" class="text-emerald-700"><font-awesome-icon :icon="['fas', 'circle-check']" class="mr-2 fa-fw"/> Завершено</span>
      </div>
    </div>

    <div class="flex flex-col  text-sm  mx-12 " v-show="state.jobsVisibility[job.id]">
      <div class="flex flex-row  text-gray-300 text-xl uppercase font-black">
<!--        <span>Кроки виконнаня задачі {{job.id}}</span>-->
      </div>
      <div class="flex flex-row bg-gray-500 text-gray-50 font-semibold  gap-2 py-1">
        <div class="flex flex-col w-5p text-center">ID </div>
        <div class="flex flex-col w-5p text-center whitespace-nowrap">Крок</div>
        <div class="flex flex-col w-10p">Сервіс</div>
        <div class="flex flex-col w-10p text-center">Прогресс</div>
        <div class="flex flex-col w-15p text-center">Старт</div>
        <div class="flex flex-col w-15p text-center">Фініш</div>
        <div class="flex flex-col w-15p text-center">Статус</div>
        <div class="flex flex-col w-25p">Коментар</div>
      </div>
      <div class="flex flex-row  bg-gray-100 text-sm text-gray-600  py-1  last:mb-4 border-b-2 border-gray-200 last:rounded-b-xl" v-for="step in job.steps" :key="step.id">
        <div class="flex flex-col w-5p text-center  ">{{ step.id }}</div>
        <div class="flex flex-col w-5p text-center  ">{{ step.storedStep.stepOrder }}</div>
        <div class="flex flex-col w-10p ">{{ step.storedStep.serviceName }}</div>
<!--        <div class="flex flex-col w-10p text-center ">{{ Number((step.progress * 100).toFixed(2)) }} %</div>-->
        <div class="flex flex-col w-10p text-center  ">
          <div class="progress-container">
            <div class="progress-bar" :style="{ width: (step.progress * 100).toFixed(2) + '%' }"></div>
            <div class="progress-text">
              {{ Number((step.progress * 100).toFixed(2)) }} %
            </div>
          </div>
        </div>

        <div class="flex flex-col w-15p text-center ">{{ step.startedAt == null ? 'невідомий' : formatDate(step.startedAt) }}</div>
        <div class="flex flex-col w-15p text-center ">{{ step.finishedAt == null ? 'невідомий' : formatDate(step.finishedAt) }}</div>
        <div v-if="step.status==='FINISHED'" class="flex flex-col text-emerald-700 font-semibold  w-15p  ">
         <span>  <font-awesome-icon :icon="['fas', 'circle-check']" class="mr-2 fa-fw"/>Завершено</span>
        </div>
        <div v-if="step.status==='IN_PROCESS' || step.status==='NEW'" class="flex flex-col text-blue-500 font-semibold   w-15p  ">
          <span><font-awesome-icon :icon="['fas', 'spinner']" class="mr-2 fa-fw"/> Обробка</span>
        </div>

        <div v-if="step.status==='FAILED'" class="flex flex-col text-red-500 font-semibold   w-15p  ">
          <span> <font-awesome-icon :icon="['fas', 'circle-exclamation']" class="mr-2 fa-fw"/>Помилка</span>
        </div>
        <div v-if="step.status==='SKIPED'" class="flex flex-col text-amber-600 font-semibold   w-15p  ">Пропущено
        </div>
<!--        <div class="flex flex-col w-25p  "> {{ step.comment }}</div>-->
        <div class="flex flex-col w-25p  "> <span class="break-words block"> {{ step.comment }}</span></div>
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

.progress-container {
  width: 100%; /* Full width */
  //background-color: #ddd; /* Grey background */
  @apply bg-gray-400 rounded-lg;
  position: relative; /* Positioning context for the text */
}

.progress-bar {
  height: 20px; /* Height of the progress bar */
  background-color: #4396bd; /* Green background */
  @apply bg-gradient-to-r from-green-700 to-green-600 rounded-lg;

}

.progress-text {
  position: absolute; /* Absolute positioning */
  top: 50%; /* Center vertically */
  left: 50%; /* Center horizontally */
  transform: translate(-45%, -50%); /* Adjust to exact center */
  //color: black; /* Text color */
  @apply text-red-50 font-semibold;
}


</style>