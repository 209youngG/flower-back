<script setup>
import { ref, computed } from 'vue';
import StarRating from '../common/StarRating.vue';

const props = defineProps({
  isOpen: Boolean,
  mode: {
    type: String, // 'create' | 'update'
    default: 'create'
  },
  productName: String,
  initialData: {
    type: Object,
    default: () => ({ rating: 5, content: '' })
  },
  loading: Boolean
});

const emit = defineEmits(['close', 'submit']);

const form = ref({
  rating: props.initialData.rating,
  content: props.initialData.content
});

const isValid = computed(() => {
  return form.value.rating > 0 && form.value.content.trim().length >= 10;
});

const handleSubmit = () => {
  if (!isValid.value) return;
  emit('submit', { ...form.value });
};
</script>

<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 transition-opacity">
    <div class="bg-white rounded-xl shadow-2xl w-full max-w-md mx-4 transform transition-all scale-100">
      <!-- Header -->
      <div class="px-6 py-4 border-b border-gray-100 flex justify-between items-center bg-gray-50 rounded-t-xl">
        <h3 class="text-lg font-semibold text-gray-800">
          {{ mode === 'create' ? '리뷰 작성' : '리뷰 수정' }}
        </h3>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600 transition-colors">
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
          </svg>
        </button>
      </div>

      <!-- Body -->
      <div class="p-6 space-y-6">
        <!-- 상품 정보 -->
        <div class="flex items-center space-x-3 text-sm text-gray-600 bg-gray-50 p-3 rounded-lg">
          <div class="w-12 h-12 bg-gray-200 rounded-md overflow-hidden flex-shrink-0">
            <!-- 이미지 placeholder -->
            <svg class="w-full h-full text-gray-400" fill="currentColor" viewBox="0 0 24 24"><path d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" /></svg>
          </div>
          <span class="font-medium line-clamp-2">{{ productName }}</span>
        </div>

        <!-- 별점 입력 -->
        <div class="flex flex-col items-center space-y-2">
          <label class="text-sm font-medium text-gray-700">이 상품 어떠셨나요?</label>
          <StarRating v-model="form.rating" size="lg" />
          <span class="text-xs text-gray-500 font-medium">
            {{ form.rating }}점 / 5점
          </span>
        </div>

        <!-- 텍스트 입력 -->
        <div class="space-y-2">
          <label class="text-sm font-medium text-gray-700 block">
            솔직한 후기를 남겨주세요
          </label>
          <div class="relative">
            <textarea
              v-model="form.content"
              rows="4"
              class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-rose-500 focus:border-rose-500 resize-none transition-shadow text-sm"
              placeholder="상품의 품질, 배송, 포장 상태 등 자세한 후기는 다른 고객들에게 큰 도움이 됩니다. (최소 10자)"
            ></textarea>
            <div class="absolute bottom-3 right-3 text-xs" :class="form.content.length < 10 ? 'text-red-500' : 'text-gray-400'">
              {{ form.content.length }}자 / 최소 10자
            </div>
          </div>
        </div>
      </div>

      <!-- Footer -->
      <div class="px-6 py-4 border-t border-gray-100 flex justify-end space-x-3 bg-gray-50 rounded-b-xl">
        <button
          @click="$emit('close')"
          class="px-4 py-2 text-sm font-medium text-gray-600 hover:bg-gray-200 rounded-lg transition-colors"
        >
          취소
        </button>
        <button
          @click="handleSubmit"
          :disabled="!isValid || loading"
          class="px-6 py-2 text-sm font-medium text-white bg-rose-500 hover:bg-rose-600 disabled:bg-gray-300 disabled:cursor-not-allowed rounded-lg shadow-sm transition-all flex items-center"
        >
          <span v-if="loading" class="mr-2">
            <svg class="animate-spin h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
          </span>
          {{ mode === 'create' ? '등록하기' : '수정하기' }}
        </button>
      </div>
    </div>
  </div>
</template>
