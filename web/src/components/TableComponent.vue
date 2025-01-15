<template>
    <div>
      <table >
        <thead>
          <tr>
            <th v-for="(header, index) in tableInfo.headers" :key="index">
              {{ header.title }}
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(row, rowIndex) in tableInfo.data" :key="rowIndex">
            <td v-for="(header, colIndex) in tableInfo.headers" :key="colIndex">
              {{ row[header.key]}}
            </td>
          </tr>
        </tbody>
      </table>
  
      <div class="pagination">
        <button :disabled="currentPage === 1" @click="changePage(currentPage - 1)">
          Précédent
        </button>
        <span>Page {{ currentPage }} sur {{ totalPages }}</span>
        <button  @click="changePage(currentPage + 1)">
          Suivant
        </button>
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
  
  <style>
  .table {
    width: 100%;
    border-collapse: collapse;
  }
  .table th,
  .table td {
    border: 1px solid #ddd;
    padding: 8px;
    text-align: left;
  }
  .pagination {
    display: flex;
    justify-content: center;
    margin-top: 10px;
  }
  button {
    margin: 0 5px;
    padding: 5px 10px;
  }
  </style>
  