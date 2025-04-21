<template>

    <v-card outlined>
        <v-card-title>
            PlaceOrder
        </v-card-title>

        <v-card-text>
            <String label="UserId" v-model="value.userId" :editMode="editMode"/>
            <Number label="Qty" v-model="value.qty" :editMode="editMode"/>
            <InventoryId offline label="InventoryId" v-model="value.inventoryId" :editMode="editMode" @change="change"/>
            <OrderStatus offline label="OrderStatus" v-model="value.orderStatus" :editMode="editMode" @change="change"/>
            <Address offline label="Address" v-model="value.address" :editMode="editMode" @change="change"/>
            <OrderItemList offline label="OrderItems" v-model="value.orderItems" :editMode="editMode" @change="change"/>
            <Date label="OrderDate" v-model="value.orderDate" :editMode="editMode"/>
        </v-card-text>

        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
                    color="deep-purple lighten-2"
                    text
                    @click="placeOrder"
            >
                PlaceOrder
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
    name: 'PlaceOrderCommand',
    components:{
    },
    props: {},
    data: () => ({
        editMode: true,
        value: {},
    }),
    created() {
        this.value.userId = '';
        this.value.qty = 0;
        this.value.inventoryId = {};
        this.value.orderStatus = {};
        this.value.address = {};
        this.value.orderItems = [];
        this.value.orderDate = '2025-04-21';
    },
    watch: {
    },
    methods: {
        placeOrder() {
            this.$emit('placeOrder', this.value);
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

