import {ref} from 'vue';

type Color = "success" | "error" | "warning" | "info"; 
const notificationBar = ref({
    show:false,
    message:"",
    color: "info" as Color,
});

function showNotification(message: string, color: Color = "info"){
    notificationBar.value = {show: true, message, color};
}

export function useNotification(){
    return {notificationBar, showNotification};
}