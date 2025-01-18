<template>
  <div class="filter-search-component">
    
    <div class="search-bar">
      <input
        v-model="searchQuery"
        type="text"
        placeholder="Rechercher..."
        class="search-input"
      />
    </div>


    <div class="filters">
      <div
        v-for="field in filterableFields"
        :key="field"
        class="filter-select"
      >
        <select
          v-model="filters[field]"
          @change="applyFilters"
        >
          <option value="">Tous les {{ field }}</option>
          <option
            v-for="value in getUniqueValues(field)"
            :key="value"
            :value="value"
          >
            {{ value }}
          </option>
        </select>
      </div>
    </div>


    <div class="results">
      <slot
        v-for="item in filteredData"
        :key="item.id"
        :item="item"
      ></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DynamicFilterSearch',

  props: {
    items: {
      type: Array,
      required: true
    },

    searchableFields: {
      type: Array,
      default: () => []
    },

    filterableFields: {
      type: Array,
      default: () => []
    }
  },

  data() {
    return {
      searchQuery: '',
      filters: {},
      filteredData: []
    }
  },

  watch: {
    searchQuery: {
      handler() {
        this.applyFilters()
      },
      debounce: 300
    },


    items: {
      handler() {
        this.initializeFilters()
        this.applyFilters()
      },
      deep: true
    }
  },

  created() {
    this.initializeFilters()
    this.applyFilters()
  },

  methods: {
    initializeFilters() {
      this.filterableFields.forEach(field => {
        this.$set(this.filters, field, '')
      })
    },


    getUniqueValues(field) {
      return [...new Set(this.items.map(item => item[field]))]
    },


    applyFilters() {
      let result = [...this.items]


      if (this.searchQuery) {
        const searchLower = this.searchQuery.toLowerCase()
        result = result.filter(item =>
          this.searchableFields.some(field =>
            String(item[field]).toLowerCase().includes(searchLower)
          )
        )
      }


      Object.entries(this.filters).forEach(([field, value]) => {
        if (value) {
          result = result.filter(item => String(item[field]) === String(value))
        }
      })

      this.filteredData = result
      this.$emit('update:filtered', result)
    }
  }
}
</script>

<style scoped>
.filter-search-component {
  padding: 1rem;
}

.search-bar {
  margin-bottom: 1rem;
}

.search-input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.filters {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  margin-bottom: 1rem;
}

.filter-select select {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  min-width: 150px;
}

.results {
  display: grid;
  gap: 1rem;
}
</style>
