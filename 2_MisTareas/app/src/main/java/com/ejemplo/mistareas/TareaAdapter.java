package com.ejemplo.mistareas;

import android.content.Context;
import android.graphics.Paint;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ejemplo.mistareas.databinding.ItemTareaBinding;

import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {

    // Usamos esta interfaz para avisar a la Activity de los eventos de cada tarjeta.
    public interface OnTareaListener {
        void onTareaPulsada(int posicion);
        void onMenuTarea(View ancla, int posicion);
        void onCrearMenuContextual(ContextMenu menu, int posicion);
    }

    private final List<Tarea> tareas;
    private final OnTareaListener listener;

    public TareaAdapter(List<Tarea> tareas, OnTareaListener listener) {
        this.tareas = tareas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTareaBinding binding = ItemTareaBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TareaViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        holder.mostrar(tareas.get(position));
    }

    @Override
    public int getItemCount() {
        return tareas.size();
    }

    class TareaViewHolder extends RecyclerView.ViewHolder {

        private final ItemTareaBinding binding;

        TareaViewHolder(ItemTareaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Detectamos el toque sobre la tarjeta.
            binding.getRoot().setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onTareaPulsada(pos);
                }
            });

            // Abrimos el menú de prioridad con el botón de tres puntos.
            binding.btnMenuTarea.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onMenuTarea(v, pos);
                }
            });

            // Usamos una pulsación larga para abrir el menú contextual.
            binding.getRoot().setOnCreateContextMenuListener((menu, v, menuInfo) -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onCrearMenuContextual(menu, pos);
                }
            });
        }

        void mostrar(Tarea tarea) {
            Context ctx = binding.getRoot().getContext();

            binding.tvTitulo.setText(tarea.getTitulo());
            binding.tvDescripcion.setText(tarea.getDescripcion());
            binding.cbCompletada.setChecked(tarea.isCompletada());

            // Ponemos o quitamos el tachado según el estado de la tarea.
            int flags = binding.tvTitulo.getPaintFlags();
            if (tarea.isCompletada()) {
                binding.tvTitulo.setPaintFlags(flags | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.getRoot().setAlpha(0.6f);
            } else {
                binding.tvTitulo.setPaintFlags(flags & ~Paint.STRIKE_THRU_TEXT_FLAG);
                binding.getRoot().setAlpha(1f);
            }

            int texto;
            int color;

            switch (tarea.getPrioridad()) {
                case Tarea.PRIORIDAD_ALTA:
                    texto = R.string.prioridad_alta;
                    color = R.color.prioridad_alta;
                    break;
                case Tarea.PRIORIDAD_BAJA:
                    texto = R.string.prioridad_baja;
                    color = R.color.prioridad_baja;
                    break;
                default:
                    texto = R.string.prioridad_media;
                    color = R.color.prioridad_media;
            }

            binding.tvPrioridad.setText(texto);
            binding.tvPrioridad.setTextColor(ContextCompat.getColor(ctx, color));
        }
    }
}
