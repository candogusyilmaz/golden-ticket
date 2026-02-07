import { useQuery } from "@tanstack/react-query";
import { fetchClient } from "../api/open-api";

// Normally I would've used $api.useQuery(...)
// But in order to simulate multiple users, I need to pass a unique ID to the query key,
// otherwise the query will be shared across all users/sessions and won't simulate properly.
export function useRemainingQuantity(sessionId: string) {
  const { data, status } = useQuery({
    queryFn: async () => (await fetchClient.GET("/api/inventory")).data,
    queryKey: ["get", "/api/inventory", sessionId],
    refetchInterval: 2000,
  });

  return { remainingQuantity: data?.remainingQuantity, status };
}
