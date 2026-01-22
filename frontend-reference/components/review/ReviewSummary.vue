<script setup>
import { computed } from 'vue';
import StarRating from '../common/StarRating.vue';

const props = defineProps({
  averageRating: {
    type: Number,
    required: true
  },
  totalCount: {
    type: Number,
    required: true
  },
  // API 응답 데이터가 있다면 사용, 없으면 랜덤 분포 (예시용)
  ratingDistribution: {
    type: Object,
    default: () => ({
      5: 0.7, // 70%
      4: 0.2, // 20%
      3: 0.05,
      2: 0.03,
      1: 0.02
    })
  }
});

const formattedRating = computed(() => props.averageRating.toFixed(1));
</script>

<template>
  <div class="bg-gray-50 rounded-xl p-6 mb-8">
    <h3 class="text-lg font-bold text-gray-800 mb-4">상품 리뷰</h3>
    <div class="flex flex-col md:flex-row items-center md:items-start space-y-6 md:space-y-0 md:space-x-12">
      <!-- Total Score -->
      <div class="flex flex-col items-center justify-center min-w-[140px]">
        <span class="text-5xl font-extrabold text-gray-900 mb-2">{{ formattedRating }}</span>
        <StarRating :modelValue="averageRating" readonly size="lg" />
        <span class="text-sm text-gray-500 mt-2">{{ totalCount.toLocaleString() }}개의 리뷰</span>
      </div>

      <!-- Divider -->
      <div class="hidden md:block w-px h-32 bg-gray-200"></div>

      <!-- Rating Distribution (Histogram) -->
      <div class="flex-1 w-full space-y-2">
        <div v-for="score in [5, 4, 3, 2, 1]" :key="score" class="flex items-center text-sm">
          <span class="w-8 font-medium text-gray-600 flex-shrink-0">{{ score }}점</span>
          <div class="flex-1 h-2.5 bg-gray-200 rounded-full overflow-hidden mx-3">
            <div
              class="h-full bg-yellow-400 rounded-full"
              :style="{ width: `${(ratingDistribution[score] || 0) * 100}%` }"
            ></div>
          </div>
          <span class="w-12 text-right text-gray-400 flex-shrink-0">
            {{ Math.round((ratingDistribution[score] || 0) * 100) }}%
          </span>
        </div>
      </div>
    </div>
  </div>
</template>
