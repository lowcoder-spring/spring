<template>
  <CallBusRefresh :destination="destination" :instance="instance" />
</template>

<script>
import CallBusRefresh from "@/call/components/call-bus-refresh"

export default {
  components: {CallBusRefresh},
  props: {
    instance: {
      type: Object,
      required: true
    }
  },
  computed: {
    destination() {
      return this.instance.registration.name.toLowerCase()
    }
  },
  data() {
    return {
    }
  },
}
</script>