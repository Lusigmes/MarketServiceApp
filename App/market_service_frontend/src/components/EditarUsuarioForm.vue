<script setup lang="ts">
import { atualizarUsuario } from '@/api/UsuarioService';
import { useNotification } from '@/composables/useNotification';
import type { UsuarioResponseInterface } from '@/types';
import { reactive } from 'vue';
import * as yup from 'yup';
    const { showNotification } = useNotification();

    interface Props{
        usuario: UsuarioResponseInterface;
    }

    const props = defineProps<Props>();
    const emit = defineEmits<{
        (e: 'salvar', payload: Partial<UsuarioResponseInterface>): void;
        (e: 'cancelar'): void;
    }>();

    const form = reactive({
        nome: props.usuario.nome || '',
        email: props.usuario.email || '',
        cep: props.usuario.cep || '',
    });
    const errors = reactive({
        nome: '',
        email: '',
        cep: '',
    });

    const schema = yup.object({
        nome: yup.string().required("Nome obrigatório").min(2, "O nome deve conter ao menos 2 caracteres"),
        email: yup.string().email("E-mail inválido").required("E-mail obrigatório"),
        cep: yup.string()
            .transform(value => value.replace(/\D/g, ''))
            .required('CEP é obrigatório')
            .length(8, 'CEP deve conter exatamente 8 números'),
    });

    const validarForm = async () => {
        try{
            await schema.validate(form, {abortEarly:false});
            Object.keys(errors).forEach(key => errors[key as keyof typeof errors] = '');
            return true;
        }catch(erro: any){
            Object.keys(errors).forEach(key => errors[key as keyof typeof errors] = '');
            if(erro.inner){
                erro.inner.forEach((e: any) => {
                    errors[e.path as keyof typeof errors] = e.message;
                });
            }
            return false;
        }
    };

    const salvar = async () => {
        const valido = await validarForm();
        if(!valido) return;

        const novosDados = {
            ...form,
        };

        try{
            const usuarioAtualizado = await atualizarUsuario(props.usuario.id, novosDados);
            emit('salvar',{...usuarioAtualizado});
            showNotification("Perfil atualizado com sucesso!", "success");
            
        }catch (error) {
            showNotification("Não foi possível atualizar o perfil.", "error");
        }
    }
    
    const cancelar = () => {
        form.nome = props.usuario.nome;
        // form.email = props.usuario.email;
        form.cep = props.usuario.cep;
        emit('cancelar');
    }

</script>


<template>
  <v-card >
    <v-card-title>Editar Perfil</v-card-title>
    <v-card-text  style="max-height: 400px; overflow-y: auto;">
      <v-text-field 
        v-model="form.nome" 
        label="Nome completo" 
        :error-messages="errors.nome"
        variant="outlined"
        outlined 
        dense 
        class="mb-3 mt-8"
      />
      
    <v-text-field 
        :model-value="props.usuario.email"
        label="E-mail"
        variant="outlined"
        outlined 
        dense 
        class="mb-3"
        readonly
        hint="O e-mail não pode ser alterado"
        persistent-hint
      />
      
      <v-text-field 
        v-model="form.cep" 
        label="CEP" 
        :error-messages="errors.cep"
        variant="outlined"
        class="mb-3"
        outlined 
        dense 
        v-mask="'#####-###'"
       
      />
    </v-card-text>
    <v-card-actions class="d-flex justify-end">
      <v-btn color="primary" @click="salvar">Salvar</v-btn>
      <v-btn text color="red" @click="cancelar">Cancelar</v-btn>
    </v-card-actions>
  </v-card>
</template>

<style scoped>
.gap-2 {
  gap: 8px;
}
</style>