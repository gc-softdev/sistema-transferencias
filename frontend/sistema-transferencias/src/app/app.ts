import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AgendamentoComponent } from './components/agendamento/agendamento';
import { ExtratoComponent } from './components/extrato/extrato';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, AgendamentoComponent, ExtratoComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class AppComponent {
  title = 'Sistema de Transferências';
  abaSelecionada: 'agendamento' | 'extrato' = 'agendamento';

  selecionarAba(aba: 'agendamento' | 'extrato'): void {
    this.abaSelecionada = aba;
  }
}

