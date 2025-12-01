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
    telefone: props.usuario.telefone || '',
    especializacao: props.usuario.especializacao || '',
});

const errors = reactive({
    nome: '',
    email: '',
    cep: '',
    telefone: '',
    especializacao: '',
});

const schema = yup.object({
    nome: yup.string().required("Nome obrigatório").min(2, "O nome deve conter ao menos 2 caracteres"),
    email: yup.string().email("E-mail inválido").required("E-mail obrigatório"),
    cep: yup.string()
        .transform(value => value.replace(/\D/g, ''))
        .required('CEP é obrigatório')
        .length(8, 'CEP deve conter exatamente 8 números'),
    telefone: yup.string().when('$isPrestador', {
        is: true,
        then: (schema) => schema
            .required('Telefone é obrigatório para prestadores')
            .transform(value => value.replace(/\D/g, ''))
            .min(10, 'Telefone deve ter pelo menos 10 dígitos'),
        otherwise: (schema) => schema.notRequired()
    }),
    especializacao: yup.string().when('$isPrestador', {
        is: true,
        then: (schema) => schema
            .required('Especialização é obrigatória para prestadores')
            .min(3, 'Especialização deve ter pelo menos 3 caracteres'),
        otherwise: (schema) => schema.notRequired()
    })
});

const isPrestador = props.usuario.tipoUsuario === 'PRESTADOR';

const validarForm = async (): Promise<boolean> => {
    try{
        await schema.validate(form, { 
            abortEarly: false,
            context: { isPrestador }
        });
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
        ...(isPrestador && {
            telefone: form.telefone ? form.telefone.replace(/\D/g, '') : undefined,
            especializacao: form.especializacao
        })   
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
    form.cep = props.usuario.cep;
    form.telefone = props.usuario.telefone || '';
    form.especializacao = props.usuario.especializacao || '';
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

      <template v-if="usuario.tipoUsuario === 'PRESTADOR'">
        <v-text-field 
            v-model="form.telefone" 
            label="Telefone" 
            :error-messages="errors.telefone"
            variant="outlined"
            class="mb-3"
            outlined 
            dense 
            v-mask="'(##) #####-####'"
        />  
        
        <v-text-field 
            v-model="form.especializacao" 
            label="Especialização" 
            :error-messages="errors.especializacao"
            variant="outlined"
            class="mb-3"
            outlined 
            dense
            placeholder="Ex: Encanador, Eletricista, Pintor"
        />
      </template>
    </v-card-text>
    <v-card-actions class="d-flex justify-end">
      <v-btn color="primary" @click="salvar">Salvar</v-btn>
      <v-btn text color="red" @click="cancelar">Cancelar</v-btn>
    </v-card-actions>
  </v-card>
</template>