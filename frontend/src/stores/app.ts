import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  // State
  const isCollapsed = ref(false)
  const isDark = ref(false)
  const loading = ref(false)

  // Actions
  function toggleCollapse() {
    isCollapsed.value = !isCollapsed.value
  }

  function setCollapsed(value: boolean) {
    isCollapsed.value = value
  }

  function toggleTheme() {
    isDark.value = !isDark.value
    document.documentElement.classList.toggle('dark', isDark.value)
  }

  function setLoading(value: boolean) {
    loading.value = value
  }

  return {
    isCollapsed,
    isDark,
    loading,
    toggleCollapse,
    setCollapsed,
    toggleTheme,
    setLoading
  }
})
