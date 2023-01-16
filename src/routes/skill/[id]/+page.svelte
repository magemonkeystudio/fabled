<script lang="ts">
  import { ProSkill } from "../../../api/proskill";

  export let data: { skill: ProSkill };
</script>

<h1>{data.skill.name}</h1>
<hr />