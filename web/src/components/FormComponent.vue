<template>
  <form @submit.prevent="handleSubmit" class="bg-white p-8 rounded shadow-lg w-full max-w-md mx-auto space-y-6">
    <h2 class="text-2xl font-bold text-gray-800 text-center mb-4">{{title}}</h2>

    <div v-for="field in fields" :key="field.name" class="space-y-2">
      <label :for="field.name" class="block text-gray-700 text-sm font-semibold">{{ field.label }}</label>

      <div v-if="['text', 'email', 'password'].includes(field.type)" class="mb-6">
        <input
          :type="field.type"
          :name="field.name"
          :placeholder="field.placeholder"
          v-model="formData[field.name]"
          class="shadow appearance-none border rounded w-full py-3 px-4 text-gray-800 leading-tight focus:outline-none focus:ring-2 focus:ring-black focus:border-transparent"
        />
      </div>

      <div v-if="field.type === 'select'" class="mb-6">
        <select
          :name="field.name"
          v-model="formData[field.name]"
          class="shadow appearance-none border rounded w-full py-3 px-4 text-gray-800 leading-tight focus:outline-none focus:ring-2 focus:ring-black focus:border-transparent"
        >
          <option v-for="option in field.options" :key="option.value" :value="option.value">
            {{ option.label }}
          </option>
        </select>
      </div>

      <div v-if="field.type === 'radio'" class="mb-6 flex flex-col space-y-2">
        <div v-for="option in field.options" :key="option.value" class="flex items-center">
          <input
            type="radio"
            :name="field.name"
            :value="option.value"
            v-model="formData[field.name]"
            class="mr-2"
          />
          <label class="text-gray-700 text-sm">{{ option.label }}</label>
        </div>
      </div>

      <div v-if="field.type === 'textarea'" class="mb-6">
        <textarea
          :name="field.name"
          v-model="formData[field.name]"
          :placeholder="field.placeholder"
          class="shadow appearance-none border rounded w-full py-3 px-4 text-gray-800 leading-tight focus:outline-none focus:ring-2 focus:ring-black focus:border-transparent"
        ></textarea>
      </div>

      <div v-if="field.type === 'checkbox'" class="mb-6 flex items-center">
        <input type="checkbox" :name="field.name" v-model="formData[field.name]" class="mr-2" />
        <label class="text-gray-700 text-sm">{{ field.label }}</label>
      </div>
    </div>

    <div class="flex items-center justify-between">
      <button
        class="bg-black hover:bg-gray-800 text-white font-bold py-2 px-6 rounded focus:outline-none focus:ring-2 focus:ring-black transition duration-300"
        type="submit"
      >
        {{ btn_text }}
      </button>
    </div>
  </form>
</template>

<script>
export default {
  name: "FormComponent",
  props: {
    fields: {
      type: Array,
      required: true,
    },
    btn_text: {
      type: String,
      required: true,
    },
    title:{
      type : String,
      required : true
    }
  },
  data() {
    return {
      formData: {},
    };
  },
  created() {
    this.fields.forEach((field) => {
      this.formData[field.name] = field.type === "checkbox" ? false : "";
    });
  },
  methods: {
    handleSubmit() {
      this.$emit("formSubmitted", this.formData);
    },
  },
};
</script>
