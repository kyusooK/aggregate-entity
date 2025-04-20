<template>

    <v-card outlined>
        <v-card-title>
            ModifyOrder
        </v-card-title>

        <v-card-text>
            <OrderItemList offline label="OrderItems" v-model="value.orderItems" :editMode="editMode" @change="change"/>
        </v-card-text>

        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
                    color="deep-purple lighten-2"
                    text
                    @click="modifyOrder"
            >
                ModifyOrder
            </v-btn>
            
            <v-btn
                    color="deep-purple lighten-2"
                    text
                    @click="close"
            >
                Close
            </v-btn>
        </v-card-actions>
    </v-card>

</template>

<script>

export default {
    name: 'ModifyOrderCommand',
    components:{
    },
    props: {},
    data: () => ({
        editMode: true,
        value: {},
    }),
    created() {
        this.value.orderItems = [];
    },
    watch: {
    },
    methods: {
        modifyOrder() {
            this.$emit('modifyOrder', this.value);
        },
        close() {
            this.$emit('closeDialog');
        },
        change() {
            this.$emit("update:modelValue", this.value);
        },
    }
}
</script>

