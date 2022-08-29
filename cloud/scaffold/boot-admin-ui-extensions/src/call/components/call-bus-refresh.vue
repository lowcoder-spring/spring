<template>
  <div class="container">
    <div class="ext-name">{{ `${destination ? destination + ' ' : ''}bus-refresh` }}</div>
    <div class="actions">
      <code>{{`POST ${busRefreshUri}`}}</code>
      <button :disabled="calling"  @click="handleCall">
        <template v-if="calling">calling</template>
        <template v-else>call</template>
      </button>
    </div>
    <div class="response">
      <div :class="['status', this.responseClass] "></div>
      <p class="content">{{ response.content }}</p>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    instance: {
      type: Object,
      required: true
    },
    destination: {
      type: String,
      default: null,
    }
  },
  computed: {
    responseClass() {
      return this.response.success === null ? 'not-executed' : (this.response.success === true ? 'success' : 'error')
    },
    busRefreshUri() {
      if (this.destination) {
        return `${this.actuatorPath}/bus-refresh/${this.destination}`
      } else {
        return `${this.actuatorPath}/bus-refresh`
      }
    }
  },
  async created() {
    this.actuatorPath = this.instance.registration['managementUrl'].replace(this.instance.registration['serviceUrl'], "")
  },
  data() {
    return {
      actuatorPath: "/actuator",
      calling: false,
      response: {
        success: null,
        content: "未执行",
      }
    }
  },
  methods: {
    handleCall() {
      this.calling = true
      this.response.success = null
      this.response.content = "执行中..."

      this.instance.axios.post(this.busRefreshUri).then(response => {
        this.response.success = true
        this.response.content = response.data ? JSON.stringify(response.data, null, 4) : "执行成功"
      }).catch(e => {
        this.response.success = false
        this.response.content = JSON.stringify(e, null, 4)
      }).finally(() => {
        this.calling = false
      })
    },
  }
}
</script>

<style scoped>
button {
  background-color: #38d1a0;
  color: #fff;

  font-size: 1rem;
  justify-content: center;
  padding: calc(.5em - 1px) 1em;
  text-align: center;
  white-space: nowrap;
  cursor: pointer;
  -webkit-box-pack: center;
  border-radius: 4px;
  border: 1px solid transparent;
}
button:disabled {
  background-color: darkgrey;
}

.container {
  padding: 30px;
  margin: 0;
}

.container .ext-name {
  color: #363636;
  font-size: 2rem;
  font-weight: 600;
  line-height: 1.125;
}
.container .actions {
  margin-top: 1rem;
  border-top: 2px solid #dbdbdb;
  padding-top: 0.5rem;
}
.container .actions button {
  margin-left: 1rem;
}
.response {
  margin-top: 1rem;
  width: 100%;
}
.response .status {
  border-radius: 50%;
  width: 1.5rem;
  height: 1.5rem;
  font-weight: bolder;
  float: left;
  background: darkgrey;

  margin-right: 1rem;
}
.response .status.not-executed {
  background: darkgrey;
  color: darkgrey;
}
.response .status.success {
  background: #38d1a0;
  color: #38d1a0;
}
.response .status.error {
  background: red;
  color: red;
}
</style>