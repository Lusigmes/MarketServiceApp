
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

export function itemListaDashboardDemandas() {
  return [
    { title: 'Demandas', disabled: false, href: '/dashboard' }
  ];
}

export function itemListaDashboardPrestadores() {
  return [
    { title: 'Demandas', disabled: false, href: '/dashboard' },
    { title: 'Prestadores', disabled: true, href: '/dashboard/prestadores' }
  ];
}

export function itemListaDashboardPropostasDosPrestadores() {
  return [
    { title: 'Demandas', disabled: false, href: '/dashboard' },
    { title: 'Propostas', disabled: true, href: '/dashboard/propostas' }
  ];
}