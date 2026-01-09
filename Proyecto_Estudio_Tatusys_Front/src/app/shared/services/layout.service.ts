import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class LayoutService {
  // Controla la visibilidad de los botones/filtros del padre
  private _showParentButtons = new BehaviorSubject<boolean>(true);

  // Exponer como observable solo lectura
  public readonly showParentButtons$: Observable<boolean> = this._showParentButtons.asObservable();

  // Getter para plantillas si prefieres usarlo de forma directa
  get showParentButtons() {
    return this._showParentButtons;
  }

  setShowParentButtons(value: boolean) {
    this._showParentButtons.next(value);
  }
}
