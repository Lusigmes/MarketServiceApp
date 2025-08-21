
export function itemListaHome(){
    return [
        {title:'Inicio', disabled: true, href: '/'}
    ]
}

export function itemListaLogin(){
    return [
        {title:'Inicio', disabled: false, href: '/'},
        {title:'Login', disabled: true, href: '/login'}
    ]
}

export function itemListaRegistro(){
    return [
        {title:'Inicio', disabled: false, href: '/'},
        {title:'Registro', disabled: true, href: '/registro'}
    ]
}

export function itemListaDashboard(){
    return [
        {title:'Tela Inicial', disabled: true, href: '/dashboard'}
    ]
}

export function itemListaDashboardDemandas() {
  return [
    { title: 'Tela Inicial', disabled: true, href: '/' },
    { title: 'Demandas', disabled: true, href: '/dashboard/demandas' }
  ];
}

export function itemListaDashboardPrestadores() {
  return [
    { title: 'Tela Inicial', disabled: true, href: '/dashboard' },
    { title: 'Prestadores', disabled: true, href: '/dashboard/prestadores' }
  ];
}
