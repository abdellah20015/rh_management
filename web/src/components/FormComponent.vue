
<template>
  <div class="flex justify-center items-center  bg-transparent p-4">
    <div class="w-full max-w-2xl">
      <form
      @submit.prevent="handleSubmit"
      class="bg-white rounded-[12px]  shadow-2xl p-8 "
      >
      <h2 class="text-3xl font-bold text-center text-gray-900 mb-6">
        {{ title }}
      </h2>
        <div 
          :class="[
            fields.length > 5
              ? 'grid grid-cols-2 gap-6' 
              : 'space-y-6 w- '
          ]"
        >
          <div 
            v-for="field in fields" 
            :key="field.name" 
            class="space-y-4"
          >
            <!-- Text, Email, Password, Number Input -->
            <div v-if="['text', 'email', 'password'].includes(field.type)">
              <label
                :for="field.name"
                class="block text-sm font-medium text-gray-700 mb-2"
              >
                {{ field.label }}
              </label>
              <InputText
                :id="field.name"
                :type="field.type"
                :name="field.name"
                :placeholder="field.placeholder"
                v-model="formData[field.name]"
                class="w-full"
              />
            </div>

            <div v-if="field.type == 'number'">
              <label
                :for="field.name"
                class="block text-sm font-medium text-gray-700 mb-2"
              >
                {{ field.label }}
              </label>
              <InputNumber
                v-model="formData[field.name]"
                :id="field.name"
                :placeholder="field.placeholder"
                :inputId="field.name"
                class="w-full"
              />
            </div>

            <!-- Other input types remain the same as in previous version -->
            <!-- Date Input -->
            <div v-if="field.type === 'date'  && !isFieldHidden(field)" class="mb-6">
              <label :for="field.name" class="block text-gray-700 text-sm font-semibold mb-2">{{ field.label }}</label>
              <input
                :type="field.type"
                :name="field.name"
                v-model="formData[field.name]"
                class="shadow appearance-none border rounded w-full py-3 px-4 text-gray-800 leading-tight focus:outline-none focus:ring-2 focus:ring-black focus:border-transparent"
              />
             </div>

            <!-- Select Input -->
            <div v-if="field.type === 'select'">
              <label class="block text-sm font-medium text-gray-700 mb-2">
                {{ field.label }}
              </label>
              <MultiSelect
                v-if="field.multiple"
                :options="field.options"
                optionLabel="label"
                optionValue="value"
                v-model="formData[field.name]"
                :placeholder="field.label"
                filter
                class="w-full"
              />
              <Select
                v-model="formData[field.name]"
                 v-if="!field.multiple"
                :options="field.options"
                optionLabel="label"
                optionValue="value"
                :placeholder= "field.label"
                class="w-full"
              />
            </div>

            <!-- Radio Input -->
            <div v-if="field.type === 'radio'" class="space-y-2">
              <label class="block text-sm font-medium text-gray-700 mb-2">
                {{ field.label }}
              </label>
              <div class="flex space-x-4">
                <div
                  v-for="option in field.options"
                  :key="option.value"
                  class="flex items-center"
                >
                  <RadioButton
                    :name="field.name"
                    :value="option.value"
                    v-model="formData[field.name]"
                  />
                  <label class="ml-2 text-sm text-gray-700">
                    {{ option.label }}
                  </label>
                </div>
              </div>
            </div>

            <!-- Textarea Input -->
            <div v-if="field.type === 'textarea'">
              <label
                :for="field.name"
                class="block text-sm font-medium text-gray-700 mb-2"
              >
                {{ field.label }}
              </label>
              <Textarea
                :name="field.name"
                v-model="formData[field.name]"
                :placeholder="field.placeholder"
                class="w-full"
              />
            </div>

            <!-- Checkbox Input -->
            <div v-if="field.type === 'checkbox'" class="flex items-center">
              <Checkbox
                :name="field.name"
                v-model="formData[field.name]"
                :binary="true"
              />
              <label :for="field.name" class="ml-2 text-sm text-gray-700">
                {{ field.label }}
              </label>
            </div>
          </div>
        </div>

        <div class="pt-6 w-full">
          <button
            type="submit"
            class="w-full py-2.5 bg-gradient-to-r bg-[#006aff]  text-white rounded-[8px]
                  hover:bg-[#006aff]
                   focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2
                   transform hover:scale-[1.02] transition-all duration-300 ease-in-out"
          >
            {{ btn_text }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script>
import InputText from "primevue/inputtext";
// import Calendar from "primevue/calendar";
import MultiSelect from "primevue/multiselect";
import Select from 'primevue/select';
import RadioButton from "primevue/radiobutton";
import Textarea from "primevue/textarea";
import Checkbox from "primevue/checkbox";
import Button from "primevue/button";
import InputNumber from 'primevue/inputnumber';
export default {
  components: {
    InputText,
    // Calendar,
    MultiSelect,
    Select,
    RadioButton,
    Textarea,
    Checkbox,
    Button,
    InputNumber
  },
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
    "formData.type": {
      handler(newVal) {
        this.fields.forEach((field) => {
          const isHidden =
            field.hidden &&
            typeof field.hidden === "function" &&
            field.hidden(this.formData);
          if (isHidden && this.formData.hasOwnProperty(field.name)) {
            delete this.formData[field.name];
          } else if (!isHidden && !this.formData.hasOwnProperty(field.name)) {
            this.formData[field.name] = "";
          }
        });
      },
      immediate: true,
    },
  },
  created() {
    this.fields.forEach((field) => {
      const isHidden =
        field.hidden &&
        typeof field.hidden === "function" &&
        field.hidden(this.formData);
      if (!isHidden) {
        this.formData[field.name] =
          this.initialData[field.name] !== undefined
            ? this.initialData[field.name]
            : field.type === "checkbox"
            ? false
            : field.type === "select" &&
              Array.isArray(this.initialData[field.name])
            ? [...this.initialData[field.name]]
            : "";
      }
    });
  },
  methods: {
    isFieldHidden(field) {
      if (field.hidden && typeof field.hidden === "function") {
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
