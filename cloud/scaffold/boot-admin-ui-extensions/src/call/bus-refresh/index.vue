<template>
  <CallBusRefresh :instance="instance" />
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
  data() {
    return {
    }
  },
}
</script>