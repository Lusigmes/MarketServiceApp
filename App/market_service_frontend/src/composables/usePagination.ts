import { ref } from "vue";

export function useDemandaPagination<T>(fetchFunction: (page: number, size: number) => Promise<{content:T[]; totalPages:number; totalElements:number}>, initialSize = 10) {    
    const items = ref<T[]>([]);
    const page = ref(0);
    const size = ref(initialSize);
    const totalPages = ref(0);
    const totalElements = ref(0);
    const loading = ref(false);
    
    const atualizarPagina = async (pagina = page.value) => {
        loading.value = true;
        try {
            const response = await fetchFunction(pagina, size.value);
            items.value = response.content;
            totalPages.value = response.totalPages;
            totalElements.value = response.totalElements;
            page.value = pagina;
        } finally {
            loading.value = false;
        }
    };
    /*
    //para botoes separados / s/ v-pagination
    const proximaPagina = async () => {
        if(page.value + 1 < totalPages.value){
            await atualizarPagina(page.value + 1);
        }
    };
    const paginaAnterior = async () => {
        if(page.value > 0){
            await atualizarPagina(page.value - 1);
        }
    };
    */
    
    return {
        items,
        page,
        size,
        totalPages,
        totalElements,
        loading,
        atualizarPagina,
        // proximaPagina,
        // paginaAnterior
    };
}

export function usePropostaPagination<T>(
    fetchFunction: (demandaId: number, page: number, size: number) => Promise<{content:T[]; totalPages:number; totalElements:number}>,
    demandaId: number,
    initialSize = 5
) {
    const items = ref<T[]>([]);
    const page = ref(0);
    const size = ref(initialSize);
    const totalPages = ref(0);
    const totalElements = ref(0);
    const loading = ref(false);

    const atualizarPagina = async (pagina = page.value) => {
        try {
            const response = await fetchFunction(demandaId, pagina, size.value);
            items.value = response.content;
            totalPages.value = response.totalPages;
            totalElements.value = response.totalElements;
            page.value = pagina;
        } finally {
            loading.value = false;
        }
    };

    return {
        items, page, size,
        totalPages, totalElements, loading,
        atualizarPagina,
    }  ;
};