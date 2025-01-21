<template>
  <form @submit.prevent="handleSubmit" class="bg-white p-8 rounded shadow-lg w-full max-w-md mx-auto space-y-6">
    <h2 class="text-2xl font-bold text-gray-800 text-center mb-4">{{ title }}</h2>

    <div v-for="field in fields" :key="field.name" class="space-y-2">
      
      <!-- Champ de type input -->
      <div v-if="['text', 'email', 'password' , 'number'].includes(field.type)" class="mb-6">
        <label :for="field.name" class="block text-gray-700 text-sm font-semibold mb-2">{{ field.label }}</label>
        <input
          :type="field.type"
          :name="field.name"
          :placeholder="field.placeholder"
          v-model="formData[field.name]"
          class="shadow appearance-none border rounded w-full py-3 px-4 text-gray-800 leading-tight focus:outline-none focus:ring-2 focus:ring-black focus:border-transparent"
        />
      </div>

    <!-- Champ de type date -->
    <div v-if="field.type === 'date'  && !isFieldHidden(field)" class="mb-6">
      <label :for="field.name" class="block text-gray-700 text-sm font-semibold mb-2">{{ field.label }}</label>
      <input
        :type="field.type"
        :name="field.name"
        v-model="formData[field.name]"
        class="shadow appearance-none border rounded w-full py-3 px-4 text-gray-800 leading-tight focus:outline-none focus:ring-2 focus:ring-black focus:border-transparent"
      />
    </div>


      <!-- Champ de type select -->
      <div v-if="field.type === 'select'" class="mb-6">
        <label :for="field.name" class="block text-gray-700 text-sm font-semibold mb-2">{{ field.label }}</label>
        <select
          :name="field.name"
          v-model="formData[field.name]"
          :multiple="field.multiple"
          class="shadow appearance-none border rounded w-full py-3 px-4 text-gray-800 leading-tight focus:outline-none focus:ring-2 focus:ring-black focus:border-transparent"
        >
          <option value="" disabled  >{{ field.label }}</option>
          
          <!-- Options dynamiques -->
          <option v-for="option in field.options" :key="option.value" :value="option.value">
            {{ option.label }}
          </option>
        </select>
      </div>

      <!-- Autres types de champ -->
      <div v-if="field.type === 'radio'" class="mb-6 flex flex-col space-y-2">
        <label :for="field.name" class="block text-gray-700 text-sm font-semibold">{{ field.label }}</label>
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
        <label :for="field.name" class="block text-gray-700 text-sm font-semibold mb-2">{{ field.label }}</label>
        <textarea
          :name="field.name"
          v-model="formData[field.name]"
          :placeholder="field.placeholder"
          class="shadow appearance-none border rounded w-full py-3 px-4 text-gray-800 leading-tight focus:outline-none focus:ring-2 focus:ring-black focus:border-transparent"
        ></textarea>
      </div>

      <div v-if="field.type === 'checkbox'" class="mb-6 flex items-center">
        <label :for="field.name" class="block text-gray-700 text-sm font-semibold mb-2">{{ field.label }}</label>
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
    title: {
      type: String,
      required: true,
    },
    initialData: {
      type: Object,
      default: () => ({}),
    },
  },
  data() {
    return {
      formData: {},
    };
  },
  watch: {
    initialData: {
      handler(newVal) {
        this.formData = { ...newVal };
      },
    },
    'formData.type': {
    handler(newVal) {
      this.fields.forEach((field) => {
        const isHidden = field.hidden && typeof field.hidden === 'function' && field.hidden(this.formData);
        if (isHidden && this.formData.hasOwnProperty(field.name)) {
          delete this.formData[field.name];
        } else if (!isHidden && !this.formData.hasOwnProperty(field.name)) {
          this.formData[field.name] = '';
        }
      });
    },
    immediate: true
  }
  },
  created() {
  this.fields.forEach((field) => {
    const isHidden = field.hidden && typeof field.hidden === 'function' && field.hidden(this.formData);
    if (!isHidden) {
      this.formData[field.name] = 
        this.initialData[field.name] !== undefined
          ? this.initialData[field.name]
          : field.type === "checkbox"
          ? false
          : field.type === "select" && Array.isArray(this.initialData[field.name])
          ? [...this.initialData[field.name]]
          : "";
    }
  });
},
  methods: {
    isFieldHidden(field) {
      if (field.hidden && typeof field.hidden === 'function') {
        return field.hidden(this.formData);
      }
      return false;
    },
    handleSubmit() {
      this.$emit("formSubmitted", this.formData);
    },
  },
};
</script>