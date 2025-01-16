<template>
  <div class="p-4">
    <!-- Table -->
    <div class="overflow-x-auto border rounded-md">
      <table class="min-w-full ">
        <!-- Table Header -->
        <thead class="bg-black">
          <tr>
            <th v-for="(header, index) in tableInfo.headers" :key="index"
              class="px-6 py-3 text-left text-sm font-medium text-white border-b border-gray-300">
              {{ header.title }}
            </th>
          </tr>
        </thead>

        <!-- Table Body -->
        <tbody>
          <tr v-for="(row, rowIndex) in tableInfo.data" :key="rowIndex"
            class="hover:bg-gray-100 transition duration-300">
            <td v-for="(header, colIndex) in tableInfo.headers" :key="colIndex"
              class="px-6 py-4 text-sm text-gray-600 border-b border-gray-300">
              <div v-if="header != 'actions'">{{ row[header.key] }}</div>
              <div class="flex" v-if="header.key === 'actions'">
                <div v-for="(button, colIndex) in tableInfo.buttons" :key="colIndex"
                  v-html="button.button" @click="button.action(row)" :class="{
                    'opacity-50 pointer-events-none': typeof button.disabled === 'function' ? button.disabled(row) : button.disabled
                  }" class="flex px-2"></div>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div class="flex justify-center">
      <div class="mt-4 flex justify-between items-center w-1/2">
        <button :disabled="currentPage === 1" @click="changePage(currentPage - 1)"
          class="px-4 py-2 bg-black text-white rounded-md hover:bg-gray-800 disabled:bg-gray-300 disabled:cursor-not-allowed transition duration-300">
          Précédent
        </button>

        <span class="text-gray-700 text-sm">
          Page {{ currentPage }} sur {{ totalPages }}
        </span>

        <button @click="changePage(currentPage + 1)"
          class="px-4 py-2 bg-black text-white rounded-md hover:bg-gray-800 disabled:bg-gray-300 disabled:cursor-not-allowed transition duration-300">
          Suivant
        </button>
      </div>
    </div>
  </div>

</template>

<script>
export default {
  props: {
    tableInfo: {
      type: Object,
      required: true,
    },
    pageSize: {
      type: Number,
      default: 10,
    },
    currentPage: {
      type: Number,
      required: true,
    },
    totalPages: {
      type: Number,
      required: true,
    },
  },
  methods: {
    changePage(newPage) {
      this.$emit("page-changed", newPage);
    },
  },
};
</script>
