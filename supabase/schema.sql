create table if not exists public.cuentas (
  id uuid primary key default gen_random_uuid(),
  nombre text not null,
  tipo text not null default 'principal',
  telefono text,
  pais_codigo text,
  activa boolean not null default true,
  created_at timestamptz not null default now()
);

create table if not exists public.usuarios (
  id uuid primary key default gen_random_uuid(),
  cuenta_id uuid references public.cuentas(id) on delete cascade,
  celular text not null,
  pais_codigo text not null default '+591',
  nombre text,
  created_at timestamptz not null default now(),
  unique (pais_codigo, celular)
);

create table if not exists public.productos (
  id uuid primary key default gen_random_uuid(),
  cuenta_id uuid references public.cuentas(id) on delete cascade,
  nombre text not null,
  codigo text,
  categoria text,
  stock integer not null default 0,
  precio numeric(12,2) not null default 0,
  costo numeric(12,2) not null default 0,
  created_at timestamptz not null default now()
);

alter table public.cuentas enable row level security;
alter table public.usuarios enable row level security;
alter table public.productos enable row level security;

create policy "lectura temporal cuentas"
on public.cuentas for select
using (true);

create policy "insert temporal cuentas"
on public.cuentas for insert
with check (true);

create policy "lectura temporal usuarios"
on public.usuarios for select
using (true);

create policy "insert temporal usuarios"
on public.usuarios for insert
with check (true);

create policy "lectura temporal productos"
on public.productos for select
using (true);

create policy "insert temporal productos"
on public.productos for insert
with check (true);
