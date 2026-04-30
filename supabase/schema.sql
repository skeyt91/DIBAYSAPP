create extension if not exists pgcrypto;

create table if not exists public.cuentas (
  id uuid primary key default gen_random_uuid(),
  auth_user_id uuid references auth.users(id) on delete cascade,
  nombre text not null,
  tipo text not null default 'principal',
  activa boolean not null default true,
  created_at timestamptz not null default now()
);

create table if not exists public.usuarios (
  id uuid primary key default gen_random_uuid(),
  auth_user_id uuid references auth.users(id) on delete cascade,
  cuenta_id uuid references public.cuentas(id) on delete cascade,
  email text,
  nombre text not null,
  pin_hash text not null,
  created_at timestamptz not null default now()
);

create table if not exists public.productos (
  id uuid primary key default gen_random_uuid(),
  auth_user_id uuid references auth.users(id) on delete cascade,
  cuenta_id uuid references public.cuentas(id) on delete cascade,
  nombre text not null,
  codigo text,
  categoria text,
  stock integer not null default 0,
  precio numeric(12,2) not null default 0,
  costo numeric(12,2) not null default 0,
  created_at timestamptz not null default now()
);

alter table public.cuentas add column if not exists auth_user_id uuid references auth.users(id) on delete cascade;
alter table public.usuarios add column if not exists auth_user_id uuid references auth.users(id) on delete cascade;
alter table public.usuarios add column if not exists pin_hash text;
alter table public.usuarios add column if not exists email text;
alter table public.productos add column if not exists auth_user_id uuid references auth.users(id) on delete cascade;

alter table public.cuentas alter column auth_user_id drop not null;
alter table public.usuarios alter column auth_user_id drop not null;
alter table public.productos alter column auth_user_id drop not null;

create index if not exists cuentas_auth_user_id_idx on public.cuentas(auth_user_id);
create index if not exists usuarios_auth_user_id_idx on public.usuarios(auth_user_id);
create index if not exists usuarios_email_idx on public.usuarios(lower(email));
create index if not exists productos_auth_user_id_idx on public.productos(auth_user_id);

alter table public.cuentas enable row level security;
alter table public.usuarios enable row level security;
alter table public.productos enable row level security;

drop policy if exists "cuentas propias select" on public.cuentas;
drop policy if exists "cuentas propias insert" on public.cuentas;
drop policy if exists "cuentas propias update" on public.cuentas;
drop policy if exists "usuarios propios select" on public.usuarios;
drop policy if exists "usuarios propios insert" on public.usuarios;
drop policy if exists "usuarios propios update" on public.usuarios;
drop policy if exists "productos propios select" on public.productos;
drop policy if exists "productos propios insert" on public.productos;
drop policy if exists "productos propios update" on public.productos;
drop policy if exists "productos propios delete" on public.productos;

create policy "cuentas propias select"
on public.cuentas for select
to anon, authenticated
using (auth_user_id = auth.uid() or auth_user_id is null);

create policy "cuentas propias insert"
on public.cuentas for insert
to anon, authenticated
with check (auth_user_id = auth.uid() or auth_user_id is null);

create policy "cuentas propias update"
on public.cuentas for update
to authenticated
using (auth_user_id = auth.uid())
with check (auth_user_id = auth.uid());

create policy "usuarios propios select"
on public.usuarios for select
to anon, authenticated
using (auth_user_id = auth.uid() or auth_user_id is null);

create policy "usuarios propios insert"
on public.usuarios for insert
to anon, authenticated
with check (auth_user_id = auth.uid() or auth_user_id is null);

create policy "usuarios propios update"
on public.usuarios for update
to authenticated
using (auth_user_id = auth.uid())
with check (auth_user_id = auth.uid());

create policy "productos propios select"
on public.productos for select
to anon, authenticated
using (auth_user_id = auth.uid() or auth_user_id is null);

create policy "productos propios insert"
on public.productos for insert
to anon, authenticated
with check (auth_user_id = auth.uid() or auth_user_id is null);

create policy "productos propios update"
on public.productos for update
to anon, authenticated
using (auth_user_id = auth.uid() or auth_user_id is null)
with check (auth_user_id = auth.uid() or auth_user_id is null);

create policy "productos propios delete"
on public.productos for delete
to anon, authenticated
using (auth_user_id = auth.uid() or auth_user_id is null);
