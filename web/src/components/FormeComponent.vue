<template>
    <form @submit.prevent="handleSubmit" >
      <div v-for="field in fields" :key="field.name">
        <label :for="field.name">{{ field.label }}</label>
  
        <div
          v-if="['text', 'email', 'password'].includes(field.type)"
          
        >
          <input
            :type="field.type"
            :name="field.name"
            :placeholder="field.placeholder"
            v-model="formData[field.name]"
            
          />
        </div>
  
        <div v-if="field.type === 'select'">
          <select
            :name="field.name"
            v-model="formData[field.name]"
            
          >
            <option
              v-for="option in field.options"
              :key="option.value"
              :value="option.value"
            >
              {{ option.label }}
            </option>
          </select>
        </div>
  
        <div v-if="field.type === 'radio'" class="mb-3">
          <div v-for="option in field.options" :key="option.value">
            <input
              type="radio"
              :name="field.name"
              :value="option.value"
              v-model="formData[field.name]"
              
            />
            <label class="form-check-label">{{ option.label }}</label>
          </div>
        </div>
  
        <div v-if="field.type === 'textarea'" class="mb-3">
          <textarea
            :name="field.name"
            v-model="formData[field.name]"
            :placeholder="field.placeholder"
           
          ></textarea>
        </div>
  
        <div v-if="field.type === 'checkbox'" class="mb-3 d-inline ms-3">
          <input
            type="checkbox"
            :name="field.name"
            v-model="formData[field.name]"
            
          />
        </div>
      </div>
  
      <button type="submit" >{{ btn_text }}</button>
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
  
  <style>
  .form-group {
    margin-bottom: 1rem;
  }
  
  .form-control {
    width: 100%;
    padding: 0.5rem;
    margin-top: 0.25rem;
  }
  
  .btn {
    margin-top: 1rem;
  }
  </style>
  