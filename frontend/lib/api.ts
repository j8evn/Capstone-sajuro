const API_BASE = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export async function apiPost<T>(path: string, body: unknown): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    const error = await res.json().catch(() => ({ message: 'API Error' }));
    throw new Error(error.message || `API Error: ${res.status}`);
  }
  return res.json();
}

export async function apiGet<T>(path: string, params?: Record<string, string>): Promise<T> {
  const url = new URL(`${API_BASE}${path}`);
  if (params) {
    Object.entries(params).forEach(([k, v]) => url.searchParams.set(k, v));
  }
  const res = await fetch(url.toString());
  if (!res.ok) {
    const error = await res.json().catch(() => ({ message: 'API Error' }));
    throw new Error(error.message || `API Error: ${res.status}`);
  }
  return res.json();
}
