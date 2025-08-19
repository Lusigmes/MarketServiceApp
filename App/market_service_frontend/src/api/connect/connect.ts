import axios from "axios";

const httpConnect = axios.create({
    baseURL:"http://localhost:8080",
    headers:{
        "Content-Type":"application/json",
    },
});

httpConnect.interceptors.request.use((config) => {
    const token = localStorage.getItem("jwt");
    if(token){
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export default httpConnect;