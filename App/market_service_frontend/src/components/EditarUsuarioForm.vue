<script setup lang="ts">
import { atualizarUsuario } from '@/api/UsuarioService';
import { useNotification } from '@/composables/useNotification';
import type { UsuarioResponseInterface } from '@/types';
import { reactive } from 'vue';
import * as yup from 'yup';

const { showNotification } = useNotification();

interface Props {
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
    nome: yup.string()
        .required("Nome é obrigatório")
        .min(2, "O nome deve conter ao menos 2 caracteres")
        .max(100, "O nome deve ter no máximo 100 caracteres"),
    email: yup.string()
        .email("E-mail inválido")
        .required("E-mail é obrigatório"),
    cep: yup.string()
        .transform(value => value ? value.replace(/\D/g, '') : '')
        .required('CEP é obrigatório')
        .length(8, 'CEP deve conter exatamente 8 números'),
    telefone: yup.string().when('$ehPrestador', {
        is: true,
        then: (schema) => schema
            .required('Telefone é obrigatório para prestadores')
            .transform(value => value ? value.replace(/\D/g, '') : '')
            .min(10, 'Telefone deve ter pelo menos 10 dígitos')
            .max(11, 'Telefone deve ter no máximo 11 dígitos'),
        otherwise: (schema) => schema.notRequired()
    }),
    especializacao: yup.string().when('$ehPrestador', {
        is: true,
        then: (schema) => schema
            .required('Especialização é obrigatória para prestadores')
            .min(3, 'Especialização deve ter pelo menos 3 caracteres')
            .max(100, 'Especialização deve ter no máximo 100 caracteres'),
        otherwise: (schema) => schema.notRequired()
    })
});

const ehPrestador = props.usuario.tipoUsuario === 'PRESTADOR';

const validarForm = async (): Promise<boolean> => {
    try {
        await schema.validate(form, { 
            abortEarly: false,
            context: { ehPrestador }
        });
        Object.keys(errors).forEach(key => errors[key as keyof typeof errors] = '');
        return true;
    } catch (erro: any) {
        Object.keys(errors).forEach(key => errors[key as keyof typeof errors] = '');
        if (erro.inner) {
            erro.inner.forEach((e: any) => {
                errors[e.path as keyof typeof errors] = e.message;
            });
        }
        return false;
    }
};

const salvar = async () => {
    const valido = await validarForm();
    if (!valido) return;

    const novosDados: any = {
        nome: form.nome,
        email: form.email,
        cep: form.cep.replace(/\D/g, ''),
    };

    if (ehPrestador) {
        novosDados.telefone = form.telefone.replace(/\D/g, '');
        novosDados.especializacao = form.especializacao;
    }

    try {
        const usuarioAtualizado = await atualizarUsuario(props.usuario.id, novosDados);
        emit('salvar', { ...usuarioAtualizado });
        showNotification("Perfil atualizado com sucesso!", "success");
    } catch (error) {
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
    <v-card class="edit-profile-card" style="height: 600px; display: flex; flex-direction: column;">
        <v-toolbar color="primary" density="compact">
            <v-toolbar-title class="text-white">
                Editar Perfil
            </v-toolbar-title>
            <v-spacer></v-spacer>
            <v-btn icon @click="cancelar" color="white" size="small">
                <v-icon>mdi-close</v-icon>
            </v-btn>
        </v-toolbar>

        <v-card-text class="pa-4" style="flex: 1; overflow-y: auto;">
            <v-form @submit.prevent="salvar">
                <div class="user-type-indicator mb-4">
                    <v-chip 
                        :color="ehPrestador ? 'green' : 'blue'" 
                        text-color="white"
                        size="small"
                        class="font-weight-bold"
                    >
                        <v-icon size="16" class="mr-1">
                            {{ ehPrestador ? 'mdi-account-wrench' : 'mdi-account' }}
                        </v-icon>
                        {{ ehPrestador ? 'PRESTADOR' : 'CLIENTE' }}
                    </v-chip>
                </div>

                <v-text-field
                    v-model="form.nome"
                    label="Nome Completo"
                    :error-messages="errors.nome"
                    variant="outlined"
                    density="comfortable"
                    class="mb-4"
                    prepend-inner-icon="mdi-account"
                    :maxlength="100"
                    counter
                    required
                />

                <v-text-field
                    :model-value="props.usuario.email"
                    label="E-mail"
                    variant="outlined"
                    density="comfortable"
                    class="mb-4"
                    prepend-inner-icon="mdi-email"
                    readonly
                    persistent-hint
                >
                    <template #append-inner>
                        <v-tooltip location="top">
                            <template #activator="{ props: tooltipProps }">
                                <v-icon
                                    v-bind="tooltipProps"
                                    color="grey"
                                    size="small"
                                    class="ml-1"
                                >
                                    mdi-information
                                </v-icon>
                            </template>
                            <span>O e-mail não pode ser alterado</span>
                        </v-tooltip>
                    </template>
                </v-text-field>

                <v-text-field
                    v-model="form.cep"
                    label="CEP"
                    :error-messages="errors.cep"
                    variant="outlined"
                    density="comfortable"
                    class="mb-4"
                    prepend-inner-icon="mdi-map-marker"
                    v-mask="'#####-###'"
                    required
                />

                <template v-if="ehPrestador">
                    <v-text-field
                        v-model="form.telefone"
                        label="Telefone"
                        :error-messages="errors.telefone"
                        variant="outlined"
                        density="comfortable"
                        class="mb-4"
                        prepend-inner-icon="mdi-phone"
                        v-mask="'(##) #####-####'"
                        required
                    />

                    <v-text-field
                        v-model="form.especializacao"
                        label="Especialização"
                        :error-messages="errors.especializacao"
                        variant="outlined"
                        density="comfortable"
                        class="mb-6"
                        prepend-inner-icon="mdi-briefcase"
                        placeholder="Ex: Encanador, Eletricista, Pintor, Pedreiro"
                        :maxlength="100"
                        counter
                        required
                    />
                </template>
            </v-form>
        </v-card-text>

        <v-card-actions class="pa-4 border-top" style="flex-shrink: 0;">
            <v-spacer></v-spacer>
            <v-btn
                color="secondary"
                variant="text"
                @click="cancelar"
                class="mr-2"
            >
                Cancelar
            </v-btn>
            <v-btn
                color="primary"
                variant="flat"
                @click="salvar"
            >
                Salvar
            </v-btn>
        </v-card-actions>
    </v-card>
</template>