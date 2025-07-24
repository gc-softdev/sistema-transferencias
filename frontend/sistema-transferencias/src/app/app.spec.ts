import { TestBed } from '@angular/core/testing';
import { AppComponent } from './app';

describe('AppComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it(`should have the 'Sistema de Transferências' title`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.title).toEqual('Sistema de Transferências');
  });

  it('should start with agendamento tab selected', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.abaSelecionada).toEqual('agendamento');
  });

  it('should switch tabs correctly', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    
    app.selecionarAba('extrato');
    expect(app.abaSelecionada).toEqual('extrato');
    
    app.selecionarAba('agendamento');
    expect(app.abaSelecionada).toEqual('agendamento');
  });

  it('should render title', () => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('Sistema de Transferências');
  });
});