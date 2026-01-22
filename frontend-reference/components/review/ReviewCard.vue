<script setup>
import { computed } from 'vue';
import StarRating from '../common/StarRating.vue';

const props = defineProps({
  review: {
    type: Object,
    required: true
  },
  currentUserMemberId: {
    type: Number,
    default: null
  }
});

const emit = defineEmits(['edit', 'delete']);

const isMyReview = computed(() => {
  return props.currentUserMemberId && props.review.memberId === props.currentUserMemberId;
});

// 날짜 포맷팅 (YYYY.MM.DD)
const formattedDate = computed(() => {
  if (!props.review.createdAt) return '';
  return new Date(props.review.createdAt).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  });
});

// 이름 마스킹 (홍길동 -> 홍*동)
const maskedName = computed(() => {
  // 실제로는 백엔드에서 마스킹해서 주는 것이 보안상 좋음
  // 여기서는 예시로 구현 (memberId만 있으므로 닉네임이 있다면 그것을 사용)
  return `User ${props.review.memberId}`; 
});
</script>

<template>
  <div class="border-b border-gray-100 py-6 last:border-0 hover:bg-gray-50 transition-colors px-4 -mx-4 rounded-lg">
    <!-- Header -->
    <div class="flex justify-between items-start mb-3">
      <div class="flex items-center space-x-2">
        <div class="flex flex-col">
          <span class="text-sm font-medium text-gray-900">{{ maskedName }}</span>
          <div class="flex items-center space-x-2 mt-0.5">
            <StarRating :modelValue="review.rating" readonly size="sm" />
            <span class="text-xs text-gray-400">{{ formattedDate }}</span>
          </div>
        </div>
      </div>

      <!-- Action Menu (본인일 때만) -->
      <div v-if="isMyReview" class="flex space-x-2">
        <button
          @click="$emit('edit', review)"
          class="text-xs text-gray-400 hover:text-gray-600 underline"
        >
          수정
        </button>
        <button
          @click="$emit('delete', review.id)"
          class="text-xs text-gray-400 hover:text-red-500 underline"
        >
          삭제
        </button>
      </div>
    </div>

    <!-- Content -->
    <p class="text-gray-700 text-sm leading-relaxed whitespace-pre-line">
      {{ review.content }}
    </p>
    
    <!-- Option (예시) -->
    <!-- <div class="mt-3">
      <span class="inline-block bg-gray-100 text-gray-500 text-xs px-2 py-1 rounded">
        구매옵션: 기본
      </span>
    </div> -->
  </div>
</template>
