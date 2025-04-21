<template>
    <v-container>
        <v-snackbar
            v-model="snackbar.status"
            :timeout="snackbar.timeout"
            :color="snackbar.color"
        >
            
            <v-btn style="margin-left: 80px;" text @click="snackbar.status = false">
                Close
            </v-btn>
        </v-snackbar>
        <div class="panel">
            <div class="gs-bundle-of-buttons" style="max-height:10vh;">
                <v-btn @click="addNewRow" @class="contrast-primary-text" small color="primary">
                    <v-icon small style="margin-left: -5px;">mdi-plus</v-icon>등록
                </v-btn>
                <v-btn :disabled="!selectedRow" style="margin-left: 5px;" @click="openEditDialog()" class="contrast-primary-text" small color="primary">
                    <v-icon small>mdi-pencil</v-icon>수정
                </v-btn>
                <v-btn style="margin-left: 5px;" @click="placeOrderDialog = true" class="contrast-primary-text" small color="primary" >
                    <v-icon small>mdi-minus-circle-outline</v-icon>place order
                </v-btn>
                <v-dialog v-model="placeOrderDialog" width="500">
                    <PlaceOrder
                        @closeDialog="placeOrderDialog = false"
                        @placeOrder="placeOrder"
                    ></PlaceOrder>
                </v-dialog>
                <v-btn :disabled="!selectedRow" style="margin-left: 5px;" @click="modifyOrderDialog = true" class="contrast-primary-text" small color="primary" :disabled="!hasRole('customer')">
                    <v-icon small>mdi-minus-circle-outline</v-icon>modify order
                </v-btn>
                <v-dialog v-model="modifyOrderDialog" width="500">
                    <ModifyOrder
                        @closeDialog="modifyOrderDialog = false"
                        @modifyOrder="modifyOrder"
                    ></ModifyOrder>
                </v-dialog>
            </div>
            <div class="mb-5 text-lg font-bold"></div>
            <div class="table-responsive">
                <v-table>
                    <thead>
                        <tr>
                        <th>Id</th>
                        <th>UserId</th>
                        <th>inventoryId</th>
                        <th>OrderStatus</th>
                        <th>OrderItems</th>
                        <th>OrderDate</th>
                        <th>Inventory</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="(val, idx) in value" 
                            @click="changeSelectedRow(val)"
                            :key="val"  
                            :style="val === selectedRow ? 'background-color: rgb(var(--v-theme-primary), 0.2) !important;':''"
                        >
                            <td class="font-semibold">{{ idx + 1 }}</td>
                            <td class="whitespace-nowrap" label="UserId">{{ val.userId }}</td>
                            <td class="whitespace-nowrap" label="OrderStatus">{{ val.orderStatus }}</td>
                            <td class="whitespace-nowrap" label="OrderItems">
                                <span v-for="(name, index) in val.orderItems" :key="index">
                                    {{ name }}<br>
                                </span>
                            </td>
                            <td class="whitespace-nowrap" label="OrderDate">{{ val.orderDate }}</td>
                            <td class="whitespace-nowrap" label="Inventory">
                                <InventoryId :editMode="editMode" v-model="val.inventoryId"></InventoryId>
                            </td>
                            <v-row class="ma-0 pa-4 align-center">
                                <v-spacer></v-spacer>
                                <Icon style="cursor: pointer;" icon="mi:delete" @click="deleteRow(val)" />
                            </v-row>
                        </tr>
                    </tbody>
                </v-table>
            </div>
            <OrderItemDetailGrid style="margin-top: 20px;" label="OrderItems" offline v-if="selectedRow" v-model="selectedRow.orderItems" :selectedRow="selectedRow"/>
        </div>
        <v-col>
            <v-dialog
                v-model="openDialog"
                transition="dialog-bottom-transition"
                width="35%"
            >
                <v-card>
                    <v-toolbar
                        color="primary"
                        class="elevation-0 pa-4"
                        height="50px"
                    >
                        <div style="color:white; font-size:17px; font-weight:700;">Order 등록</div>
                        <v-spacer></v-spacer>
                        <v-icon
                            color="white"
                            small
                            @click="closeDialog()"
                        >mdi-close</v-icon>
                    </v-toolbar>
                    <v-card-text>
                        <Order :offline="offline"
                            :isNew="!value.idx"
                            :editMode="true"
                            :inList="false"
                            v-model="newValue"
                            @add="append"
                        />
                    </v-card-text>
                </v-card>
            </v-dialog>
            <v-dialog
                v-model="editDialog"
                transition="dialog-bottom-transition"
                width="35%"
            >
                <v-card>
                    <v-toolbar
                        color="primary"
                        class="elevation-0 pa-4"
                        height="50px"
                    >
                        <div style="color:white; font-size:17px; font-weight:700;">Order 수정</div>
                        <v-spacer></v-spacer>
                        <v-icon
                            color="white"
                            small
                            @click="closeDialog()"
                        >mdi-close</v-icon>
                    </v-toolbar>
                    <v-card-text>
                        <div>
                            <String label="UserId" v-model="selectedRow.userId" :editMode="true"/>
                            <Date label="OrderDate" v-model="selectedRow.orderDate" :editMode="true"/>
                            <InventoryId offline label="inventoryId" v-model="selectedRow.inventoryId" :editMode="true"/>
                            <OrderStatus offline label="OrderStatus" v-model="selectedRow.orderStatus" :editMode="true"/>
                            <OrderItemDetailGrid label="OrderItems" offline v-model="selectedRow.orderItems" :editMode="true"/>
                            <v-divider class="border-opacity-100 my-divider"></v-divider>
                            <v-layout row justify-end>
                                <v-btn
                                    width="64px"
                                    color="primary"
                                    @click="save"
                                >
                                    수정
                                </v-btn>
                            </v-layout>
                        </div>
                    </v-card-text>
                </v-card>
            </v-dialog>
        </v-col>
    </v-container>
</template>

<script>
import { ref } from 'vue';
import { useTheme } from 'vuetify';
import BaseGrid from '../base-ui/BaseGrid.vue'


export default {
    name: 'orderGrid',
    mixins:[BaseGrid],
    components:{
    },
    data: () => ({
        path: 'orders',
        placeOrderDialog: false,
        modifyOrderDialog: false,
    }),
    watch: {
    },
    methods:{
        async placeOrder(params){
            try{
                var path = "placeOrder".toLowerCase();
                var temp = await this.repository.invoke(this.selectedRow, path, params)
                // 스넥바 관련 수정 필요
                // this.$EventBus.$emit('show-success','place order 성공적으로 처리되었습니다.')
                for(var i = 0; i< this.value.length; i++){
                    if(this.value[i] == this.selectedRow){
                        this.value[i] = temp.data
                    }
                }
                this.placeOrderDialog = false
            }catch(e){
                console.log(e)
            }
        },
        async modifyOrder(params){
            try{
                var path = "modifyOrder".toLowerCase();
                var temp = await this.repository.invoke(this.selectedRow, path, params)
                // 스넥바 관련 수정 필요
                // this.$EventBus.$emit('show-success','modify order 성공적으로 처리되었습니다.')
                for(var i = 0; i< this.value.length; i++){
                    if(this.value[i] == this.selectedRow){
                        this.value[i] = temp.data
                    }
                }
                this.modifyOrderDialog = false
            }catch(e){
                console.log(e)
            }
        },
    }
}

</script>