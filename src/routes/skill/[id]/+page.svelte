<script lang="ts">
  import type { ProSkill } from "../../../api/types";

  export let data: { skill: ProSkill };
</script>

<h1>{data.skill.name}</h1>
<hr />