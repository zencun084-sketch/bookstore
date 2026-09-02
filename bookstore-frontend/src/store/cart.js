import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getCartList } from '@/api/cart'

export const useCartStore = defineStore('cart', () => {
  const items = ref([])
  const count = computed(() => items.value.reduce((sum, item) => sum + item.quantity, 0))

  async function fetchCart() {
    try {
      const res = await getCartList()
      items.value = res.data || []
    } catch (e) {
      items.value = []
    }
  }

  function clear() {
    items.value = []
  }

  return { items, count, fetchCart, clear }
})
